package com.ainsg.lwfx.service

import com.ainsg.lwfx.util.{ HadoopTool, TimeTool }
import com.ainsg.lwfx.config.OTTConf
import org.slf4j.{ Logger, LoggerFactory };
import scala.collection.mutable.ArrayBuffer
import com.ainsg.lwfx.common.Spark._
import com.ainsg.lwfx.common.Spark.spark.implicits._
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.SaveMode
import org.apache.hadoop.fs.FileStatus
import com.ainsg.lwfx.service.UserInfoService

case class OTTString(
  USER_NAME: String,
  NET_NUM: String)

object OTTStaticService {
  
  val LOG: Logger = LoggerFactory.getLogger(OTTStaticService.getClass)
  
  def totalData(cityValue: String, count: Int, startDay: String): Unit = {
    if ("month".equals(OTTConf.totalType)) {
//      val pathList = TimeTool.PreNmonth(startDay, count)
//      LOG.info("Statistic month(统计月份):" + pathList.toString())
//      pathList.foreach(date => monthStatistics(cityValue, date))
      LOG.info("Statistic month(统计月份):" + startDay)
      monthStatistics(cityValue, startDay)
    } else if ("week".equals(OTTConf.totalType)) { //待实现

    }
  }
    /**
   * 按月统计
   */
  def monthStatistics(cityValue: String, month: String): Unit = {
    val citylist = getCityArray(month, cityValue, OTTConf.usageOTTPath);
    statisticsByCity(month, null)
  }
  
  /**
   * 计算month 月份下 cityCode 值，返回字符串数组["01","02","03"]
   */
  def getCityArray(month: String, cityValue: String, basePath: String): ArrayBuffer[String] = {
    var result = new ArrayBuffer[String]()
    if ("".equals(cityValue) || null == cityValue || "no".equals(cityValue)) {

    } else if ("all".equals(cityValue)) {
      //ComplaintConf.usageBPPPPath + "/" + month 下 子目录
      result = HadoopTool.getDirNameABFromPath(basePath + "/" + month)
    } else {
      val citys = cityValue.split(",")
      citys.foreach(citycode => result.append(citycode))
    }
    result
  }
  
  def statisticsByCity(month: String, cityCode: String): Unit = {
    var sourceDir = OTTConf.usageOTTPath + "/" + month
    var viewName = "usageBPPP"
    var cityCodeField = ""
    var cityDir = ""
    if (null != cityCode && !"".equals(cityCode)) {
      sourceDir = OTTConf.usageOTTPath + "/" + month + "/" + cityCode
      cityCodeField = ""
      cityDir = "/" + cityCode
    }
    LOG.info("start statistics for path:" + sourceDir)
    loadData(month, sourceDir, viewName, cityCodeField)
    saveMonth(viewName, month, cityDir, cityCodeField)
    spark.catalog.dropTempView(viewName)
  }
  
  /**
   * 加载采集数据并对日期，状态进行转换处理
   */
  def loadData(month: String, sourceDir: String, viewName: String, cityCodeField: String): String = {
    LOG.info("Load " + sourceDir + " datas start.(数据开始加载)")
    val usageOTT = spark.read.format("csv")
      .option("header", "true") //csv第一行有属性的话"true"，没有就是"false"
//      .option("inferSchema", true.toString) //自动推断属性列的数据类型。
      .load(sourceDir)
      .selectExpr("USER_NAME", "NET_NUM")
      .as[OTTString]
//    LOG.info("Load {} datas completed(数据加载完成): {}", sourceDir, usageOTT.count())
    usageOTT.createOrReplaceTempView(viewName);
    viewName
  }
  
  /**
   * 合计月数据
   */
  def saveMonth(viewName: String, month: String, cityDir: String, cityCodeField: String) = {
    LOG.info("Total month datas(合计月数据).")
    val ottThreshold = OTTConf.ottThreshold
    val dataMonth = spark.sql("SELECT USER_NAME," +
      "(CASE WHEN NET_NUM <= " + ottThreshold + " THEN 2 ELSE 1 END) AS OTT_NUM FROM " + viewName)
//    HadoopTool.mkDir(ComplaintConf.monthStatisticPath, month + cityDir)
    dataMonth.write.option("header", true).mode(SaveMode.Overwrite).format("csv").save(OTTConf.monthStatisticPath + "/" + month + cityDir)
//    dataMonth.show()
//    LOG.info("Total month datas completed(合计月数据完成):{}", dataMonth.count())
  }
  
}