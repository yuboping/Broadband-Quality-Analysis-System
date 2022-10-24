package com.ainsg.lwfx.service
import scala.collection.mutable.ArrayBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SaveMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.ainsg.lwfx.common.Spark.spark
import com.ainsg.lwfx.util.{TimeTool, JdbcOracleHelper, ToolsUtil,FileUtil, HadoopTool }
import com.ainsg.lwfx.clean.config.CleanConf
import com.ainsg.lwfx.config.TerminalConf
import java.io.File
import com.ainsg.lwfx.transformer.DataColumns
object TerminalService {
  
  val LOG: Logger = LoggerFactory.getLogger(TerminalService.getClass)
  /** 字符分割符 */
  val splitStr: String = ","
  
  /**
   * 处理主函数
   */
  def terminalStatistic(date: String, citycode: String, startDay: String): Unit = {
    /** yyyyMM */
//    var month = date.substring(0, 6)
//    LOG.info("date is " + month)
    val cycle: ArrayBuffer[String] = TimeTool.PreNmonth(startDay, 1)
    var month = cycle(0)
    //删除质差终端用户数据临时目录下文件
    var userTmpDir = TerminalConf.terminalUserTmpDir + "/" + month
    HadoopTool.deleteFile(userTmpDir)
    //话单数据处理 存在地市目录
    userdetStatistic(month, citycode)
//    userdetStatistic("20191007", month, citycode)
    //认证失败数据处理
    userauthfailStatistic(date, month)
//    userauthfailStatistic("20191015", month)
    //数据汇总、去重
    dealAllData(month)
    //入库
//    loadDB(startDay)
  }
  
  def loadDB(startDay: String): Unit = {
    var month = startDay.substring(4, 6)
    var viewName = "company_bq_" + month
    loadData(TerminalConf.terminalUserDir + "/" + startDay, viewName)
    var sql = "select USERNAME,AREANO,IFNULL(Organization_Name,'未知厂家') AS COMPANY_NAME,AUTHFAIL_FLAG,IFNULL(SHORTTIME_FLAG,'0') AS SHORTTIME_FLAG,OFTENDOWN_FLAG from " + 
              viewName + " where AUTHFAIL_FLAG+IFNULL(SHORTTIME_FLAG,0)+OFTENDOWN_FLAG > 0"
    LOG.info("最终入COMPANY_USER_BQ_{MM}库SQL：{}", sql)
    var bqData = spark.sql(sql)
    bqData = bqData.dropDuplicates("USERNAME", "AREANO")
    bqData.show()
    spark.catalog.dropTempView(viewName)
    // 入库：COMPANY_USER_BQ_{MM}
    if(!bqData.rdd.isEmpty()) {
      val bqSql = "INSERT INTO COMPANY_USER_BQ_" + month + " (USER_NAME,CITY_CODE,COMPANY_NAME,AUTHFAIL_FLAG,SHORTTIME_FLAG,OFTENDOWN_FLAG) VALUES (?,?,?,?,?,?)"
      JdbcOracleHelper.truncateTable("COMPANY_USER_BQ_" + month)
      JdbcOracleHelper.insertWithRDD(bqData, bqSql)
      LOG.info("数据入COMPANY_USER_BQ_{}库结束.", month)
    }
  }
  
  /**
   * 话单数据处理
   */
  def userdetStatistic(month: String, citycode: String): Unit = {
    var keyname = TerminalConf.userdet_name
    var dataDir = TerminalConf.userdetPath + "/" + month
    var isCity = CleanConf.getProp("clean.business."+keyname+".isCity")
    if("1".equals(isCity)) {
      var citys = HadoopTool.getDirNameABFromPath(dataDir)
      if(!ToolsUtil.isNullStr(citycode) && !"all".equals(citycode)) {
        //处理citycode参数文件
        citys = ToolsUtil.getSplitArrayBuffer(citycode, splitStr)
      }
      if(!citys.isEmpty) {
        //加载数据
        citys.foreach(code => {
          processUserDet(month, dataDir, code)
        })
    }
    } else {
      processUserDet(month, dataDir, "")
    }
    
  }
  
  def processUserDet(month: String, dataDir: String, citycode: String): Unit = {
    var viewName = "user_date_" + citycode
    var sourceDir = dataDir
    if(!"".equals(citycode)) {
      sourceDir = dataDir + "/" + citycode
    }
    if(!HadoopTool.isExitDir(sourceDir)) {
      LOG.error("目录:"+sourceDir+"不存在 is not exist")
      return
    }
    var data = loadData(sourceDir, viewName)
    /** 数据清洗： 去除正常下线数据 ,补齐下次上线时间字段 */
    cleanUserDet(month, citycode, viewName)
    /** 频繁掉线终端  */
    var oftenDownData = oftenDown(citycode, viewName)
    /** 短时频繁上下线行为  */
    var shortTimeDownData = shortTimeDown(citycode, viewName)
    mergeUserDetDown(oftenDownData, shortTimeDownData, month, citycode)
    spark.catalog.dropTempView(viewName)
  }
  
  /**
   * 加载话单数据
   */
  def loadData(sourceDir: String, viewName: String): DataFrame = {
    var usageBPPP: DataFrame = null
    try{
      LOG.info("Load " + sourceDir + " datas start.(数据开始加载)")
      usageBPPP = spark.read.format("csv")
        .option("header", "true") //csv第一行有属性的话"true"，没有就是"false"
        .option("inferSchema", false.toString) //自动推断属性列的数据类型。
        .load(sourceDir)
      LOG.info("Load " + sourceDir + " datas completed(数据加载完成):" + usageBPPP.rdd.isEmpty())
//      usageBPPP.show()
      usageBPPP.createOrReplaceTempView(viewName)
    }catch{
       case e: Exception => {
         usageBPPP = null
       }  
    }  
    usageBPPP
  }
  
  def cleanUserDet(month: String, citycode: String, viewName: String): Unit = {
    var sql = "select AREANO,USERNAME,DOWNREASON,MACK," +
              "to_timestamp(STARTTIME,'yyyyMMddHHmmss') as onLineTime," +
              "to_timestamp(STOPTIME,'yyyyMMddHHmmss') as offLineTime," +
              "LEAD(to_timestamp(STARTTIME,'yyyyMMddHHmmss'),1,to_timestamp('2099-01-01','yyyy-MM-dd'))" +
              " over(PARTITION BY AREANO,USERNAME ORDER BY STARTTIME) as nextOnLineTime from " +
              viewName + " t1 where t1.DOWNREASON not in('1','4','5') " +
              "AND substr(t1.STARTTIME,0,6)='" + month + "'"
    var excutSql = "select t2.*," +
                   "(cast(t2.offLineTime as Long)-cast(t2.onLineTime as Long))/60 as totalOnLineTime," +
                   "(cast(t2.nextOnLineTime as Long)-cast(t2.offLineTime as Long))/60 " +
                   " as timeBetweenOffAndOn from ("+sql+") t2"
    LOG.info("数据清洗citycode["+citycode+"]SQL："+excutSql)
    val cleanData = spark.sql(excutSql)
//    cleanData.show()
    cleanData.createOrReplaceTempView(viewName)
    LOG.info("数据清洗citycode["+citycode+"]结束--data citycode["+citycode+"] clean end")
  }
  
  def oftenDown(citycode: String, viewName: String): DataFrame = {
    var sql = "select USERNAME,AREANO,MACK,(CASE WHEN COUNT(*) > 10 THEN 1 ELSE 0 END) AS OFTENDOWN_FLAG,COUNT(*) AS OFTENDOWN_COUNT from " + viewName + " group by USERNAME,AREANO,MACK" // > 10
    LOG.info("频繁掉线终端 citycode[" + citycode + "],SQL：" + sql)
    val oftenDownData = spark.sql(sql)
    LOG.info("频繁掉线终端 data show:")
//    oftenDownData.show()
    
//    oftenDownData.write.option("header", true).mode(SaveMode.Overwrite).csv(TerminalConf.terminalUserDir + "/" + month + "/" + citycode)
    LOG.info("频繁掉线终端 citycode["+citycode+"] 执行结束")
    oftenDownData
  }
  
  def shortTimeDown(citycode: String, viewName: String): DataFrame = {
    var sql = "select USERNAME,AREANO,(CASE WHEN COUNT(*) > 5 THEN 1 ELSE 0 END) AS SHORTTIME_FLAG,COUNT(*) AS SHORTTIME_COUNT from " + viewName +
              " where totalOnLineTime<10 and timeBetweenOffAndOn<5 group by USERNAME,AREANO"
    LOG.info("短时频繁上下线终端 citycode["+citycode+"]SQL："+sql)
    val shortTimeData = spark.sql(sql)
    LOG.info("短时频繁上下线终端data show:")
//    shortTimeData.show()
    LOG.info("短时频繁上下线终端 citycode["+citycode+"] 执行结束")
    shortTimeData
  }
  
  def mergeUserDetDown(oftenDownData: DataFrame, shortTimeDownData: DataFrame, month: String, citycode: String): Unit = {
    LOG.info("频繁掉线终端 oftenDownData show :")
//    oftenDownData.show()
    LOG.info("短时频繁上下线终端 shortTimeDownData show :")
//    shortTimeDownData.show()
    LOG.info("频繁掉线终端、短时频繁上下线终端合并展示 show :")
    val totalData = oftenDownData.join(shortTimeDownData, Seq("USERNAME","AREANO"), "left")
//    totalData.show()
    //写入临时目录 写死
    totalData.write.option("header", true).mode(SaveMode.Append).csv(TerminalConf.terminalUserTmpDir + "/" + month + "/userdet")
  }
  
  /**
   * 认证失败数据处理
   */
  def userauthfailStatistic(date: String, month: String): Unit = {
    // 认证失败信息
    var keyname = TerminalConf.userauthfail
    var sourceDir = TerminalConf.userauthfailPath + "/" + month
    if(!HadoopTool.isExitDir(sourceDir)) {
      LOG.error("目录:"+sourceDir+"不存在 is not exist")
      return
    }
    
    var userInfoData = UserInfoService.getUserInfo()
    if(null == userInfoData) {
      LOG.error("用户信息为空")
      return
    }
//    userInfoData.show()
    var userTable = "users_info"
    userInfoData.createOrReplaceTempView(userTable)
    
    var viewName = "userauthfail_"+month
    loadData(sourceDir, viewName)
    var sql = "select USERNAME,count(*) AS AUTHFAIL_COUNT from " + viewName + " group by USERNAME" // > 50
    LOG.info("认证失败终端 SQL："+sql)
    val rtData = spark.sql(sql)
    rtData.createOrReplaceTempView(viewName)
    LOG.info("认证失败终端 data show：")
//    rtData.show()
    // 用户信息数据
    sql = "select t2.USERNAME,t2.AREANO,(CASE WHEN t1.AUTHFAIL_COUNT > 50 THEN 1 ELSE 0 END) AS AUTHFAIL_FLAG,t1.AUTHFAIL_COUNT from " + viewName + " t1 join " + userTable + " t2 on t1.USERNAME = t2.USERNAME" 
    //right outer join  left outer join join
    LOG.info("认证失败终端关联 SQL："+sql)
    val rstData = spark.sql(sql)
//    rstData.show()
    spark.catalog.dropTempView(viewName)
    spark.catalog.dropTempView(userTable)
    //写入临时目录
    rstData.write.option("header", true).mode(SaveMode.Append).csv(TerminalConf.terminalUserTmpDir + "/" + month + "/authfail")
  }
  
  
  
  
  def dealAllData(month: String): Unit = {
    /**
     * 读取临时目录数据
     *  */
    var sourceDir = TerminalConf.terminalUserTmpDir + "/" + month
    if(!HadoopTool.isExitDir(sourceDir)) {
      LOG.error("目录:"+sourceDir+"不存在 is not exist")
      return
    }
    
    sourceDir = TerminalConf.terminalUserTmpDir + "/" + month + "/authfail"
    var authfailViewName = "authfail_" + month
    var authfailData = loadData(sourceDir, authfailViewName)
    
    sourceDir = TerminalConf.terminalUserTmpDir + "/" + month + "/userdet"
    var userdetViewName = "userdet_" + month
    var userdetData = loadData(sourceDir, userdetViewName)
    
    var ieeePath = TerminalConf.ieeePath
    var ieeeViewName = "ieee_" + month
    var ieeeData = loadData(ieeePath, ieeeViewName)
    
//    if(null==data || data.rdd.isEmpty()) {
//      LOG.info("去重 data is null")
//      spark.catalog.dropTempView(viewName)
//      FileUtil.deleteDirChildFile(TerminalConf.terminalUserDir + "/" + month)
//      data.write.option("header", true).mode(SaveMode.Overwrite).csv(TerminalConf.terminalUserDir + "/" + month)
//      return
//    }
    
    var sql = "select t1.USERNAME,t1.AREANO,t3.Organization_Name,0 AS BADQUALITY,IFNULL(t2.AUTHFAIL_FLAG,0) AS AUTHFAIL_FLAG,IFNULL(t2.AUTHFAIL_COUNT,0) AS AUTHFAIL_COUNT," + 
              "IFNULL(t1.SHORTTIME_FLAG,0) AS SHORTTIME_FLAG,IFNULL(t1.SHORTTIME_COUNT,0) AS SHORTTIME_COUNT,IFNULL(t1.OFTENDOWN_FLAG,0) AS OFTENDOWN_FLAG,IFNULL(t1.OFTENDOWN_COUNT,0) AS OFTENDOWN_COUNT from " + 
              userdetViewName + " t1 left join " + authfailViewName + " t2 on t1.USERNAME = t2.USERNAME and t1.AREANO = t2.AREANO " + 
              "left join " + ieeeViewName + " t3 on SUBSTR(upper(regexp_replace(t1.MACK, ':', '')),1,6)=t3.Assignment"
    LOG.info("去重 SQL："+sql)
    val rstData = spark.sql(sql)
    rstData.dropDuplicates("USERNAME", "AREANO")
//    rstData.show()
    spark.catalog.dropTempView(authfailViewName)
    spark.catalog.dropTempView(userdetViewName)
    spark.catalog.dropTempView(ieeeViewName)
    //写入临时目录
    rstData.write.option("header", true).mode(SaveMode.Overwrite).csv(TerminalConf.terminalUserDir + "/" + month)
    // 入库：CES_TERMINAL_CHARACTER_BQ
//    if(!rstData.rdd.isEmpty()) {
//      var insertSql = "INSERT INTO CES_TERMINAL_CHARACTER_BQ(USERNAME,AREANO,BADQUALITY) VALUES (?,?,?)"
//      JdbcOracleHelper.truncateTable("CES_TERMINAL_CHARACTER_BQ")
//      JdbcOracleHelper.insertWithRDD(rstData, insertSql)
//    }
  }
  
  def main(args: Array[String]): Unit = {
        /**
     * 数据处理逻辑：
     * 今天处理昨天的数据，每月月初跑一次
     * 若传日期参数，则根据日期参数处理，若无参数，则执行上述操作
     * 日期格式： yyyyMMdd
     * 数据来源： 3A话单 userdet_20190831.txt 、3A认证失败数据  userauthfail_20190915.txt
     * 3A话单： 	短时频繁上下线行为、频繁掉线终端
     * 3A认证失败数据： 单宽带账号单日认证失败次数＞50次
     */
    /** yyyyMMdd */
    var date = TimeTool.yesterday();
    date = "20190830"
    var citycode = "0002"
    citycode = ""
    if (args.length >= 2) {
      citycode = args(2)
    }
    if (args.length >= 1) {
      date = args(0)
    }
//    terminalStatistic("20190915","")
    val fileNameArray: ArrayBuffer[String] = HadoopTool.getFileNameABFromPath("hdfs://127.0.0.1:9000/")
    for (filename <- fileNameArray) {
      println(filename)
    }
//    UserInfoService.getUserInfo()
  }
}