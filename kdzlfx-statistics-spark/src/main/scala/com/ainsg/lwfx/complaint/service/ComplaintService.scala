package com.ainsg.lwfx.complaint.service

import com.ainsg.lwfx.common.Spark._
import com.ainsg.lwfx.common.Spark.spark.implicits._
import com.ainsg.lwfx.util.{ HadoopTool, TimeTool }
import org.apache.spark.sql.{ DataFrame, SaveMode }
import com.ainsg.lwfx.complaint.config.ComplaintConf
import org.slf4j.{ Logger, LoggerFactory }
import scala.collection.mutable.ArrayBuffer
import com.ainsg.lwfx.transformer.DataColumns
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.Connection
import com.ainsg.lwfx.util.JdbcOracleHelper

/**
 * 特征计算
 */
object ComplaintService {
  
  val LOG: Logger = LoggerFactory.getLogger(ComplaintService.getClass)
  /**
   * 计算用户特征值以及用户初始状态，同时更新上月特征表用户状态
   * 地市取值 cityValue : all 全部地市，01,02 部分地市，no 不存在地市,默认 no
   * 统计方式 totalType：合并类型（month=按月，week=按周），默认按月
   * 统计周期数 count：合计开始日期往前N个月/周，默认为3
   * startDay：开始日期（按月则用yyyymm格式，按周则为yyyyww格式），默认为当前月
   */
  def featureStatistic(city: String, count: Int, startDay: String): Unit = {
    if ("month".equals(ComplaintConf.totalType)) {
      val cycle: ArrayBuffer[String] = TimeTool.PreNmonth(startDay, count)
      LOG.info("Computing cycle is(计算周期):" + cycle)
      val citys = ComplaintStatService.getCityArray(cycle(0), city, ComplaintConf.usageComplaintPath)
      LOG.info("Computing city is(计算地市):" + citys)
      if (citys.isEmpty) {
        featureStatisticByCity("", startDay, cycle)
      } else {
        citys.foreach(city => featureStatisticByCity(city, startDay, cycle))
      }
    } else if ("week".equals(ComplaintConf.totalType)) { //待实现

    }
  }
  /**
   * 按地市对用户进行特征值计算
   */
  def featureStatisticByCity(city: String, startDay: String, cycle: ArrayBuffer[String]) = {
    chekMonthData(city, cycle)
    saveCurruntFeature(city, startDay, cycle)
  }

  /**
   * 检查月合计数据是否存在，不存在则进行月数据合计
   */
  def chekMonthData(city: String, cycle: ArrayBuffer[String]) = {
    LOG.info("Check total datas is exist(检查合计数据是否存在).")
    cycle.foreach(month => {
//      val files = HadoopTool.getFileNameABFromPath(
//        ComplaintConf.monthStatisticPath + "/" + month + "/" + city)
//      if (files.length == 0) {
        LOG.info(ComplaintConf.monthStatisticPath + "/" + month + "/" + city + " has no files.")
        ComplaintStatService.totalData(city, 1, month)
//      }
    })
  }
  /**
   * 计算当月用户特征及可预测状态
   */
  def saveCurruntFeature(city: String, startDay: String, cycle: ArrayBuffer[String]) = {
    val monthFirstDay = TimeTool.monthFirstDay(startDay, "yyyyMM")
    val featuresData = loadDetailDatas(city, cycle)
    .join(countAccount(city, cycle), Seq("USER_NAME","FAILURE_REGION"), "left")
    .join(countFailure1(city, cycle), Seq("USER_NAME","FAILURE_REGION"), "left")
    .join(countFirstReason(city, cycle), Seq("USER_NAME","FAILURE_REGION"), "left")
    val month = startDay.substring(4, 6)
    JdbcOracleHelper.truncateTable("CES_COMPLAINT_CHARACTER_" + month)
//    featuresData.show()
//    LOG.info("final feature is(最终用户特征数据):" + featuresData.count())
    featuresData.write.option("header", true).mode(SaveMode.Overwrite).format("csv").save(ComplaintConf.featurePath + "/" + startDay + "/" + city)
    var data = spark.read.format("csv").option("header", true)
                      .load(ComplaintConf.featurePath + "/" + startDay + "/" + city)
                      .createOrReplaceTempView("complaintTotalData")
    val select_complaint_sql: String = "SELECT USER_NAME,FAILURE_REGION," + 
    "SUM(COMPLAINT_TOTAL) AS COMPLAINT_TOTAL,SUM(ACCOUNT_IPTV_MONTH) AS ACCOUNT_IPTV_MONTH,SUM(ACCOUNT_BROADBAND_MONTH) AS ACCOUNT_BROADBAND_MONTH,SUM(FAILURE1_PRODUCT_MONTH) AS FAILURE1_PRODUCT_MONTH," + 
    "SUM(FAILURE1_BASE_MONTH) AS FAILURE1_BASE_MONTH,SUM(FAILURE1_NETWORK_MONTH) AS FAILURE1_NETWORK_MONTH,SUM(FAILURE1_BUSINESS_MONTH) AS FAILURE1_BUSINESS_MONTH,SUM(REASON_CONTENT_MONTH) AS REASON_CONTENT_MONTH," + 
    "SUM(REASON_TVPLATFORM_MONTH) AS REASON_TVPLATFORM_MONTH,SUM(REASON_MARKETING_MONTH) AS REASON_MARKETING_MONTH,SUM(REASON_MANMADE_MONTH) AS REASON_MANMADE_MONTH,SUM(REASON_NETWORK_MONTH) AS REASON_NETWORK_MONTH,SUM(REASON_BOSS_MONTH) AS REASON_BOSS_MONTH," + 
    "SUM(REASON_USER_MONTH) AS REASON_USER_MONTH,SUM(REASON_MAINTAIN_MONTH) AS REASON_MAINTAIN_MONTH,SUM(REASON_OTHER_MONTH) AS REASON_OTHER_MONTH,complaintHealth(SUM(COMPLAINT_TOTAL)) AS HEALTH_VAL" +
    " FROM complaintTotalData GROUP BY USER_NAME,FAILURE_REGION"
    val featurecomplaintData = spark.sql(select_complaint_sql)
    val sql = "INSERT INTO CES_COMPLAINT_CHARACTER_" + month + " (USER_NAME,CITY_CODE,COMPLAINT_TOTAL,IPTV_TOTAL,BROADBAND_TOTAL,FA_PRODUCTQUALITY,FA_BASESERVICE,FA_NETWORKQUALITY,FA_BUSINESSMARKETING,OR_CONTENT,OR_TVPLATFORM,OR_MARKETING,OR_MANMADE,OR_NETWORK,OR_BOSS,OR_USER,OR_EQUIPMENTMAINTENANCE,OR_OTHER,HEALTH_VAL) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
    JdbcOracleHelper.insertWithRDD(featurecomplaintData, sql)
  }

  /**
   * 投诉清单特征（用户月投诉总次数）
   */
  def loadDetailDatas(city: String, cycle: ArrayBuffer[String]): DataFrame = {
    LOG.info("Start count total feature(开始计算用户月投诉总次数).")
    loadDatas(ComplaintConf.monthStatisticPath, city, cycle, "csv").createOrReplaceTempView("monthData")
    //    --月投诉总次数——计算
    val select_total_sql: String = "SELECT USER_NAME,FAILURE_REGION," +
      "SUM(COMPLAINT_SUM) AS COMPLAINT_TOTAL" +
      " FROM monthData group by USER_NAME,FAILURE_REGION"
    val featuretotal = spark.sql(select_total_sql)
//    featuretotal.show()
    featuretotal
  }
  
  def countAccount(city: String, cycle: ArrayBuffer[String]): DataFrame = {
    LOG.info("Start count account feature(开始计算用户月互联网电视和家宽投诉次数).")
    loadDatas(ComplaintConf.monthStatisticPath, city, cycle, "csv").createOrReplaceTempView("accountData")
    //    --月互联网电视和家宽投诉次数——计算
    val select_account_sql: String = "SELECT USER_NAME,FAILURE_REGION,SUM(ACCOUNT_IPTV_MONTH) AS ACCOUNT_IPTV_MONTH," + 
      "SUM(ACCOUNT_BROADBAND_MONTH) AS ACCOUNT_BROADBAND_MONTH" + 
      " FROM (SELECT USER_NAME,FAILURE_REGION," +
      "SUM(CASE WHEN ACCOUNT='IPTV' THEN COMPLAINT_SUM ELSE 0 END) AS ACCOUNT_IPTV_MONTH," +
      "SUM(CASE WHEN ACCOUNT='BROADBAND' THEN COMPLAINT_SUM ELSE 0 END) AS ACCOUNT_BROADBAND_MONTH" +
      " FROM accountData group by USER_NAME,FAILURE_REGION,ACCOUNT) ACCOUNT group by ACCOUNT.USER_NAME,ACCOUNT.FAILURE_REGION"
    val featureAccount = spark.sql(select_account_sql)
//    featureAccount.show()
    featureAccount
  }
  
  def countFailure1(city: String, cycle: ArrayBuffer[String]): DataFrame = {
    LOG.info("Start count failure1 feature(开始计算故障现象1特征).")
    loadDatas(ComplaintConf.monthStatisticPath, city, cycle, "csv").createOrReplaceTempView("countFailure1")
    //--月故障现象1——计算
    val select_total_sql: String = 
      "SELECT USER_NAME,FAILURE_REGION,SUM(FAILURE1_PRODUCT_MONTH) AS FAILURE1_PRODUCT_MONTH," +
              "SUM(FAILURE1_BASE_MONTH) AS FAILURE1_BASE_MONTH,SUM(FAILURE1_NETWORK_MONTH) AS FAILURE1_NETWORK_MONTH," +
              "SUM(FAILURE1_BUSINESS_MONTH) AS FAILURE1_BUSINESS_MONTH" +
      " FROM (SELECT USER_NAME,FAILURE_REGION," + 
      "SUM(CASE WHEN FAILURE1='产品质量' THEN COMPLAINT_SUM ELSE 0 END) AS FAILURE1_PRODUCT_MONTH," +
      "SUM(CASE WHEN FAILURE1='基础服务' THEN COMPLAINT_SUM ELSE 0 END) AS FAILURE1_BASE_MONTH," +
      "SUM(CASE WHEN FAILURE1='网络质量' THEN COMPLAINT_SUM ELSE 0 END) AS FAILURE1_NETWORK_MONTH," +
      "SUM(CASE WHEN FAILURE1='业务营销' THEN COMPLAINT_SUM ELSE 0 END) AS FAILURE1_BUSINESS_MONTH" +
      " FROM countFailure1 group by USER_NAME,FAILURE_REGION,FAILURE1) FAILURE group by FAILURE.USER_NAME,FAILURE.FAILURE_REGION"
    val featureFailure1 = spark.sql(select_total_sql)
//    featureFailure1.show()
    featureFailure1
  }
  
  def countFirstReason(city: String, cycle: ArrayBuffer[String]): DataFrame = {
    LOG.info("Start count first degree reason feature(开始计算一级原因特征).")
    loadDatas(ComplaintConf.monthStatisticPath, city, cycle, "csv").createOrReplaceTempView("countFirstReason")
    //--月一级原因——计算
    val select_reason_sql: String = 
      "SELECT USER_NAME,FAILURE_REGION,SUM(REASON_CONTENT_MONTH) AS REASON_CONTENT_MONTH,SUM(REASON_TVPLATFORM_MONTH) AS REASON_TVPLATFORM_MONTH," +
      "SUM(REASON_MARKETING_MONTH) AS REASON_MARKETING_MONTH,SUM(REASON_MANMADE_MONTH) AS REASON_MANMADE_MONTH,SUM(REASON_NETWORK_MONTH) AS REASON_NETWORK_MONTH,SUM(REASON_BOSS_MONTH) AS REASON_BOSS_MONTH," +
      "SUM(REASON_USER_MONTH) AS REASON_USER_MONTH,SUM(REASON_MAINTAIN_MONTH) AS REASON_MAINTAIN_MONTH,SUM(REASON_OTHER_MONTH) AS REASON_OTHER_MONTH " + 
      "FROM (SELECT USER_NAME,FAILURE_REGION," + 
      "SUM(CASE WHEN FIRST_DEGREE_REASON='内容原因' THEN COMPLAINT_SUM ELSE 0 END) AS REASON_CONTENT_MONTH," +
      "SUM(CASE WHEN FIRST_DEGREE_REASON='牌照方问题或电视平台故障' THEN COMPLAINT_SUM ELSE 0 END) AS REASON_TVPLATFORM_MONTH," +
      "SUM(CASE WHEN FIRST_DEGREE_REASON='前台或市场原因' THEN COMPLAINT_SUM ELSE 0 END) AS REASON_MARKETING_MONTH," +
      "SUM(CASE WHEN FIRST_DEGREE_REASON='人为破坏或电力问题' THEN COMPLAINT_SUM ELSE 0 END) AS REASON_MANMADE_MONTH," +
      "SUM(CASE WHEN FIRST_DEGREE_REASON='网络原因' THEN COMPLAINT_SUM ELSE 0 END) AS REASON_NETWORK_MONTH," +
      "SUM(CASE WHEN FIRST_DEGREE_REASON='业务管理系统和支撑系统问题' THEN COMPLAINT_SUM ELSE 0 END) AS REASON_BOSS_MONTH," +
      "SUM(CASE WHEN FIRST_DEGREE_REASON='用户原因' THEN COMPLAINT_SUM ELSE 0 END) AS REASON_USER_MONTH," +
      "SUM(CASE WHEN FIRST_DEGREE_REASON='装维原因' THEN COMPLAINT_SUM ELSE 0 END) AS REASON_MAINTAIN_MONTH," +
      "SUM(CASE WHEN FIRST_DEGREE_REASON='其他原因' THEN COMPLAINT_SUM ELSE 0 END) AS REASON_OTHER_MONTH" +
      " FROM countFirstReason group by USER_NAME,FAILURE_REGION,FIRST_DEGREE_REASON) REASON group by REASON.USER_NAME,REASON.FAILURE_REGION"
    val featureReason = spark.sql(select_reason_sql)
//    featureReason.show()
    featureReason
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
//    LOG.info("Load datas completed(数据加载完成):" + data.count())
    data
  }

  def getCityStr(city: String) = {
    var ctiystr = city + " AS CITY_CODE"
    if (city == "") {
      ctiystr = "0" + " AS CITY_CODE"
    }
    ctiystr
  }
  
  def main(args: Array[String]): Unit = {
    var cityValue = "no"
    var startDay = TimeTool.month()
    var count = 1
    if (args.length >= 3 && args(2).length == 6) {
      startDay = args(2)
    }
    if (args.length >= 2) {
      count = Integer.parseInt(args(1))
    }
    if (args.length >= 1) {
      cityValue = args(0)
    }
    LOG.info("Start feature calculation(特征值计算开始).")
    LOG.info("Running params is(运行参数为):cityValue=" + cityValue + ",count=" + count + ",startDay=" + startDay)
    featureStatistic(cityValue, count, startDay)
    LOG.info("End feature calculation(特征值计算结束).")
  }
}