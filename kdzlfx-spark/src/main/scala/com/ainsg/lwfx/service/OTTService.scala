package com.ainsg.lwfx.service

import com.ainsg.lwfx.common.Spark._
import com.ainsg.lwfx.common.Spark.spark.implicits._
import com.ainsg.lwfx.util.{ HadoopTool, TimeTool }
import org.apache.spark.sql.{ DataFrame, SaveMode }
import com.ainsg.lwfx.config.OTTConf
import org.slf4j.{ Logger, LoggerFactory }
import scala.collection.mutable.ArrayBuffer
import com.ainsg.lwfx.transformer.DataColumns
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.Connection
import com.ainsg.lwfx.util.JdbcOracleHelper
import com.ainsg.lwfx.service.UserInfoService

object OTTService {
  
  val LOG: Logger = LoggerFactory.getLogger(OTTService.getClass)
  
  def featureStatistic(city: String, count: Int, startDay: String): Unit = {
    if ("month".equals(OTTConf.totalType)) {
      val cycle: ArrayBuffer[String] = TimeTool.PreNmonth(startDay, count)
      LOG.info("Computing cycle is(计算周期):{}", cycle)
      featureStatisticByCity("", startDay, cycle)
    } else if ("week".equals(OTTConf.totalType)) { //待实现

    }
  }
  
  def featureStatisticByCity(city: String, startDay: String, cycle: ArrayBuffer[String]) = {
    chekMonthData(city, cycle)
    saveCurruntFeature(city, startDay, cycle)
//    updateCurruntFeature(city, startDay, cycle)
  }
  
  def chekMonthData(city: String, cycle: ArrayBuffer[String]) = {
    LOG.info("Check total datas is exist(检查合计数据是否存在).")
    cycle.foreach(month => {
//      val files = HadoopTool.getFileNameABFromPath(
//        ComplaintConf.monthStatisticPath + "/" + month + "/" + city)
//      if (files.length == 0) {
        LOG.info(OTTConf.monthStatisticPath + "/" + month + "/" + city + " has no files.")
        OTTStaticService.totalData(city, 1, month)
//      }
    })
  }
  
  def saveCurruntFeature(city: String, startDay: String, cycle: ArrayBuffer[String]) = {
    val monthFirstDay = TimeTool.monthFirstDay(startDay, "yyyyMM")
    val featuresData = loadDetailDatas(city, cycle)
    val userInfo = UserInfoService.getUserInfo()
    val ottDataView = "ottData"
    val userInfoDataView = "userInfoData"
    featuresData.createOrReplaceTempView(ottDataView)
    userInfo.createOrReplaceTempView(userInfoDataView)
    var sql = "select t1.USERNAME,t1.AREANO,IFNULL(t2.OTT_NUM,0) AS OTT_NUM," + 
        "(CASE WHEN t2.OTT_NUM IS NULL THEN 80 WHEN t2.OTT_NUM = 1 THEN 100 ELSE 50 END) AS HEALTH_VAL from userInfoData t1 left join ottData t2 on t1.USERNAME = t2.USER_NAME"
    LOG.info("OTT关联 SQL：{}", sql)
    val ottData = spark.sql(sql)
    spark.catalog.dropTempView(ottDataView)
    spark.catalog.dropTempView(userInfoDataView)
    ottData.write.option("header", true).mode(SaveMode.Overwrite).format("csv").save(OTTConf.featurePath + "/" + startDay + "/" + city)
//    ottData.show()
    LOG.info("final feature is(最终用户特征数据):{}", ottData.count())
    val month = startDay.substring(4, 6)
    LOG.info("month of startDay:{}", month)
    val insertSql = "INSERT INTO CES_BEHAVIOUR_CHARACTER_" + month + " (USER_NAME,CITY_CODE,OTTONLY,HEALTH_VAL) VALUES (?,?,?,?)"
    JdbcOracleHelper.truncateTable("CES_BEHAVIOUR_CHARACTER_" + month)
    JdbcOracleHelper.insertWithRDD(ottData, insertSql)
  }
  
  /**
   * 仅使用OTT业务清单特征（用户月仅使用OTT业务）
   */
  def loadDetailDatas(city: String, cycle: ArrayBuffer[String]): DataFrame = {
    LOG.info("Start count detail feature(开始计算用户月仅使用OTT业务).")
    loadDatas(OTTConf.monthStatisticPath, city, cycle, "csv").createOrReplaceTempView("monthData")
    //    --月仅使用OTT业务——计算
    val ottThreshold = OTTConf.ottThreshold
    val select_total_sql: String = "SELECT USER_NAME,OTT_NUM FROM monthData"
    val featuretotal = spark.sql(select_total_sql)
    featuretotal
  }
  
  /**
   * 根据文件格式加载数据，参数:上级目录 月份/周列表
   */
  def loadDatas(parent: String, city: String, cycle: ArrayBuffer[String], format: String = "parquet",
                header: String = "true", inferSchema: String = "false") = {
    var i = 0
    var data = spark.read
      .format(format)
      .option("header", header)
      .option("inferSchema", inferSchema)
      .load(parent + "/" + cycle(i) + "/" + city)
    for (i <- 1 to cycle.length - 1) {
      data = data.union(
        spark.read
          .format(format)
          .option("header", header)
          .option("inferSchema", inferSchema)
          .load(parent + "/" + cycle(i) + "/" + city))
    }
//    data.show()
//    LOG.info("Load datas completed(数据加载完成):{}", data.count())
    data
  }
  
  def main(args: Array[String]): Unit = {
    var startDay = TimeTool.month()
    var count = 1
    
    LOG.info("Start feature calculation(特征值计算开始).")
    LOG.info("Running params is(运行参数为):count={},startDay={}", count, startDay)
    featureStatistic("", count, startDay)
    LOG.info("End feature calculation(特征值计算结束).")
  }
  
}