package com.ainsg.lwfx.service
import scala.collection.mutable.ArrayBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SaveMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.ainsg.lwfx.common.Spark.spark
import com.ainsg.lwfx.config.TerminalConf
import com.ainsg.lwfx.util.JdbcOracleHelper
import com.ainsg.lwfx.util.TimeTool

/**
 * 入库整合
 */
object UnionService {
  val LOG: Logger = LoggerFactory.getLogger(UnionService.getClass)
  def main(args: Array[String]): Unit = {
    var cityValue = "no"
    var startDay = TimeTool.month()
    var count = 1
    var date = TimeTool.yesterday();
    if (args.length >= 3 && args(2).length == 6) {
      startDay = args(2)
    }
    if (args.length >= 3 && args(2).length == 6) {
      date = args(3)
    }
    if (args.length >= 2) {
      count = Integer.parseInt(args(1))
    }
    if (args.length >= 1) {
      cityValue = args(0)
    }
    val cycle: ArrayBuffer[String] = TimeTool.PreNmonth(startDay, count)
    LOG.info("Start feature calculation(特征值计算开始).")
    LOG.info("Running params is(运行参数为):cityValue=" + cityValue + ",count=" + count + ",startDay=" + startDay)
    TerminalService.terminalStatistic(date, cityValue, startDay)
    IgatewayService.featureStatistic(cityValue, count, startDay)
    //    TerminalService.loadData(TerminalConf.terminalUserDir + "/" + date.substring(0, 6), "terminalData")
    var terminalData = TerminalService.loadData(TerminalConf.terminalUserDir + "/" + cycle(0), "terminalData")
    LOG.info("UnionService:terminalData datas:" + terminalData.count())
    var sql = ""
    if (null == terminalData || terminalData.rdd.isEmpty()) {
      LOG.info("terminalData is null.")
      sql = "SELECT USERNAME,AREANO,'0' as BADQUALITY, INTELIGENTGATEWAY,SUBDEVICES FROM igatewayData"
    } else {
      sql = "SELECT t1.USERNAME,t1.AREANO,t2.Organization_Name,calcBQ(IFNULL(t2.AUTHFAIL_FLAG,0),IFNULL(t2.SHORTTIME_FLAG,0),IFNULL(t2.OFTENDOWN_FLAG,0)) AS BADQUALITY,t1.INTELIGENTGATEWAY,t1.SUBDEVICES," + 
        "calcHealthValue(IFNULL(t2.AUTHFAIL_FLAG,0),IFNULL(t2.SHORTTIME_FLAG,0),IFNULL(t2.OFTENDOWN_FLAG,0)) AS HEALTH_VAL," +
        "IFNULL(t2.AUTHFAIL_FLAG,0) AS AUTHFAIL_FLAG,IFNULL(t2.AUTHFAIL_COUNT,0) AS AUTHFAIL_COUNT,IFNULL(t2.SHORTTIME_FLAG,0) AS SHORTTIME_FLAG," + 
        "IFNULL(t2.SHORTTIME_COUNT,0) AS SHORTTIME_COUNT,IFNULL(t2.OFTENDOWN_FLAG,0) AS OFTENDOWN_FLAG,IFNULL(t2.OFTENDOWN_COUNT,0) AS OFTENDOWN_COUNT," + 
        "calcBQ(0,IFNULL(t2.SHORTTIME_FLAG,0),IFNULL(t2.OFTENDOWN_FLAG,0)) AS ABNORMALDOWN_FLAG FROM igatewayData t1 LEFT JOIN terminalData t2 ON t2.USERNAME = t1.USERNAME and t2.AREANO = t1.AREANO"
    }

    var igatewayDataD = TerminalService.loadData(TerminalConf.igatewayFeaturePath + "/" + startDay, "igatewayData")
    LOG.info("UnionService:igatewayDataD datas:" + igatewayDataD.count())
    var insertData = spark.sql(sql)
    var distinctData = insertData.dropDuplicates("USERNAME", "AREANO")
    distinctData.write.option("header", true).mode(SaveMode.Overwrite).csv(TerminalConf.terminalPath + "/" + startDay)
    LOG.info("UnionService:insertData datas:" + distinctData.count())
    
    TerminalService.loadData(TerminalConf.terminalPath + "/" + startDay, "terminalViewName")
    sql = "select USERNAME,AREANO,BADQUALITY,INTELIGENTGATEWAY,SUBDEVICES,HEALTH_VAL,AUTHFAIL_FLAG,AUTHFAIL_COUNT,SHORTTIME_FLAG,SHORTTIME_COUNT,OFTENDOWN_FLAG,OFTENDOWN_COUNT,ABNORMALDOWN_FLAG from terminalViewName"
    var terminalCharData = spark.sql(sql)    
    //    insertData.show()
    var month = startDay.substring(4, 6)
    //    month = "01"
    val insertSql = "INSERT INTO CES_TERMINAL_CHARACTER_" + month + " (USER_NAME,CITY_CODE,BADQUALITY,INTELIGENTGATEWAY,SUBDEVICES,HEALTH_VAL,AUTHFAIL_FLAG,AUTHFAIL_COUNT,SHORTTIME_FLAG,SHORTTIME_COUNT,OFTENDOWN_FLAG,OFTENDOWN_COUNT,ABNORMALDOWN_FLAG) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"
    JdbcOracleHelper.truncateTable("CES_TERMINAL_CHARACTER_" + month)
    JdbcOracleHelper.insertWithRDD(terminalCharData, insertSql)

    TerminalService.loadData(TerminalConf.terminalPath + "/" + startDay, "companyViewName")
    sql = "select USERNAME,AREANO,IFNULL(Organization_Name,'未知厂家') AS COMPANY_NAME,AUTHFAIL_FLAG,SHORTTIME_FLAG,OFTENDOWN_FLAG from companyViewName where BADQUALITY=1"
    var companyCharData = spark.sql(sql)  
    val bqSql = "INSERT INTO COMPANY_USER_BQ_" + month + " (USER_NAME,CITY_CODE,COMPANY_NAME,AUTHFAIL_FLAG,SHORTTIME_FLAG,OFTENDOWN_FLAG) VALUES (?,?,?,?,?,?)"
    JdbcOracleHelper.truncateTable("COMPANY_USER_BQ_" + month)
    JdbcOracleHelper.insertWithRDD(companyCharData, bqSql)
    LOG.info("数据入COMPANY_USER_BQ_{}库结束.", month)
    
    LOG.info("End feature calculation(特征值计算结束).")
  }
}