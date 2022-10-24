package com.ainsg.lwfx.features.service
import scala.collection.mutable.ArrayBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SaveMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.ainsg.lwfx.common.Spark.spark
import com.ainsg.lwfx.features.config.FeaturesConf
import com.ainsg.lwfx.util.JdbcOracleHelper
import com.ainsg.lwfx.util.TimeTool
/**
 * 特征计算
 */
object FeatureService {
  val LOG: Logger = LoggerFactory.getLogger(FeatureService.getClass)
  /**
   * 计算用户特征值以及用户初始状态，同时更新上月特征表用户状态
   * 地市取值 cityValue : all 全部地市，01,02 部分地市，no 不存在地市,默认 no
   * 统计方式 totalType：合并类型（month=按月，week=按周），默认按月
   * 统计周期数 count：合计开始日期往前N个月/周，默认为3
   * startDay：开始日期（按月则用yyyymm格式，按周则为yyyyww格式），默认为当前月
   */
  def featureStatistic(city: String, count: Int, startDay: String): Unit = {
    if ("month".equals(FeaturesConf.totalType)) {
      val cycle: ArrayBuffer[String] = TimeTool.PreNmonth(startDay, count)
      LOG.info("Computing cycle is(计算周期):" + cycle)
      val citys = StatisticService.getCityArray(cycle(0), city, FeaturesConf.userdetPath)
      LOG.info("Computing city is(计算地市):" + citys)
      if (citys.isEmpty) {
        featureStatisticByCity("", startDay, cycle)
      } else {
        citys.foreach(city => featureStatisticByCity(city, startDay, cycle))
      }
    } else if ("week".equals(FeaturesConf.totalType)) { //待实现

    }
  }
  /**
   * 按地市对用户进行特征值计算
   */
  def featureStatisticByCity(city: String, startDay: String, cycle: ArrayBuffer[String]) = {
    chekMonthData(city, cycle)
    saveCurruntFeature(city, startDay, cycle)
    updatePreviousFeature(city, cycle)
  }

  /**
   * 检查月合计数据是否存在，不存在则进行月数据合计
   */
  def chekMonthData(city: String, cycle: ArrayBuffer[String]) = {
    LOG.info("Check total datas is exist(检查合计数据是否存在).")
    cycle.foreach(month => {
      //      val files = HadoopTool.getFileNameABFromPath(
      //        FeaturesConf.monthStatisticPath + "/" + month + "/" + city)
      //      if (files.length == 0) {
      //      LOG.info(FeaturesConf.monthStatisticPath + "/" + month + "/" + city + " has no files.")
      StatisticService.totalData(city, 1, month)
      //      }
    })
  }
  /**
   * 计算当月用户特征及可预测状态
   */
  def saveCurruntFeature(city: String, startDay: String, cycle: ArrayBuffer[String]) = {
    val monthFirstDay = TimeTool.monthFirstDay(startDay, "yyyyMM")
    var featuresData = loadDetailDatas(city, cycle)
      .join(countUnusedTime(monthFirstDay, city, cycle), Seq("USERNAME", "AREANO"), "left")
    val status = countCurruntStatus(featuresData, city, cycle)
    featuresData = featuresData.join(status, "USERNAME")
    featuresData = featuresData.join(calcHealthVal(featuresData, startDay), Seq("USERNAME", "AREANO"), "left")
    //    LOG.info("final feature is(最终用户特征数据):" + featuresData.count())
    //    featuresData.show()
    if (city == "") {
      featuresData.write.option("header", true).mode(SaveMode.Overwrite).format("csv").save(FeaturesConf.featurePath + "/" + startDay)
    } else {
      featuresData.write.option("header", true).mode(SaveMode.Overwrite).format("csv").save(FeaturesConf.featurePath + "/" + startDay + "/" + city)
    }
    val month = startDay.substring(4, 6)
    val sql = "INSERT INTO CES_USER_CHARACTER_" + month + " (USER_NAME,CITY_CODE,DIAL_SUM_MONTH,DIAL_MEAN_MONTH,DIAL_MEDIAN_MONTH,DIAL_SD2_MONTH,DIAL_ENTROPY_MONTH,TIMELEN_SUM_MONTH,TIMELEN_MEAN_MONTH,TIMELEN_MEDIAN_MONTH,TIMELEN_SD2_MONTH,TIMELEN_ENTROPY_MONTH,OUTOCTETS_SUM_MONTH,OUTOCTETS_MEAN_MONTH,OUTOCTETS_MEDIAN_MONTH,OUTOCTETS_SD2_MONTH,OUTOCTETS_ENTROPY_MONTH,INOCTETS_SUM_MONTH,INOCTETS_MEAN_MONTH,INOCTETS_MEDIAN_MONTH,INOCTETS_SD2_MONTH,INOCTETS_ENTROPY_MONTH,OUTPACKETS_SUM_MONTH,OUTPACKETS_MEAN_MONTH,OUTPACKETS_MEDIAN_MONTH,OUTPACKETS_SD2_MONTH,OUTPACKETS_ENTROPY_MONTH,INPACKETS_SUM_MONTH,INPACKETS_MEAN_MONTH,INPACKETS_MEDIAN_MONTH,INPACKETS_SD2_MONTH,INPACKETS_ENTROPY_MONTH,UNUSE_TIME,STATUS,STATUS_M,HEALTH_VAL) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
    JdbcOracleHelper.truncateTable("CES_USER_CHARACTER_" + month)
    JdbcOracleHelper.insertWithRDD(featuresData, sql)
    featuresData.show()
  }
  /**
   * 更新上月用户特征表状态
   */
  def updatePreviousFeature(city: String, cycle: ArrayBuffer[String]) = {
    loadDatas(FeaturesConf.userhisPath, city, cycle, "csv", "true", "true").createOrReplaceTempView("prevSubscribe")
    LOG.info("Start update last month's status(更新上月特征表状态).")
    //    val files = HadoopTool.getFileNameABFromPath(FeaturesConf.featurePath + "/" + cycle(0) + "/" + city)
    //    if (!files.isEmpty) {
    var featuresData = loadDatas(FeaturesConf.featurePath, city, ArrayBuffer(cycle(0)), "csv", "true", "true") //上月特征表
    val user_status0 = featuresData.filter("STATUS=0").select("USERNAME") //特征表中状态为0的用户
    val user_hasDetail = spark.sql("select USERNAME from beforeMonthData") //上月有话单用户
    val user_cancel = spark.sql("select USERNAME from prevSubscribe where OTYPE =3 ") //上月销户用户
    val nonstatus0 = featuresData.filter("STATUS!=0").select("USERNAME", "STATUS", "STATUS_M") //特征表中状态不为0的用户
    val s0_noDetail = user_status0.except(user_hasDetail) //状态为0且上月无话单用户：status=1,status_m=3
    val s0_cancel = user_status0.intersect(user_cancel) //状态为0并且上月销户的用户
    val s0_other = user_status0.except(s0_noDetail.union(s0_cancel)) //状态为0，且有话单，无销户
    val status = nonstatus0.union(s0_noDetail.selectExpr("USERNAME", "1 AS STATUS", "3 AS STATUS_M")) //上月无话单用户：status=1,status_m=3
      .union(s0_cancel.selectExpr("USERNAME", "1 AS STATUS", "1 AS STATUS_M")) //状态为0并且上月销户的用户：status=1,status_m=2
      .union(s0_other.selectExpr("USERNAME", "1 AS STATUS", "1 AS STATUS_M")) //状态为0并且上月销户的用户：status=1,status_m=2
      .toDF("USERNAME", "STATUS", "STATUS_M")
    featuresData = featuresData.drop("STATUS", "STATUS_M")
    featuresData = featuresData.join(status, "USERNAME")
    //    featuresData.show()
    if (city == "") {
      featuresData.write.option("header", true).mode(SaveMode.Overwrite).format("csv").save(FeaturesConf.featurePath + "/" + cycle(0) + "/update")
    } else {
      featuresData.write.option("header", true).mode(SaveMode.Overwrite).format("csv").save(FeaturesConf.featurePath + "/" + cycle(0) + "/update/" + city)
    }
    val month = cycle(0).substring(4, 6)
    val sql = "UPDATE CES_USER_CHARACTER_" + month + " SET STATUS = ?,STATUS_M = ? WHERE USER_NAME = ? AND CITY_CODE = ?"
    val paramters = Array("STATUS", "STATUS_M", "USERNAME", "AREANO")
    JdbcOracleHelper.updateWithRDD(featuresData, sql, paramters)
    //    }
  }

  /**
   * 加载宽带清单特征（月/周拨号次数、连接时长、上下行流量、上下行包）
   */
  def loadDetailDatas(city: String, cycle: ArrayBuffer[String]): DataFrame = {
    LOG.info("Start count detail feature(开始计算用户月/周拨号次数、连接时长、上下行流量、上下行包等特征).")
    loadDatas(FeaturesConf.monthStatisticPath, city, cycle).createOrReplaceTempView("monthData")
    val select_month_sql: String = "SELECT USERNAME,AREANO," +
      //    --月拨号次数——计算
      "ROUND(SUM(DIAL_SUM),4) AS DIAL_SUM_MONTH," +
      "round(AVG(DIAL_SUM),4) AS DIAL_MEAN_MONTH," +
      "round(percentile(DIAL_SUM,0.5),4) AS DIAL_MEDIAN_MONTH," +
      "round(myDecode(AVG(DIAL_SUM),0,0,STDDEV(DIAL_SUM)/AVG(DIAL_SUM)),4) as DIAL_SD2_MONTH," +
      "round(SUM(myDecode(DIAL_SUM,0,0,DIAL_SUM*LOG(EXP(1),DIAL_SUM))),4) AS DIAL_ENTROPY_MONTH," +
      //    --月连接时长——计算
      "round(SUM(TIMELEN_SUM),4) AS TIMELEN_SUM_MONTH," +
      "round(AVG(TIMELEN_SUM),4) AS TIMELEN_MEAN_MONTH," +
      "round(percentile(TIMELEN_SUM,0.5),4) AS TIMELEN_MEDIAN_MONTH," +
      "round(myDecode(AVG(TIMELEN_SUM),0,0,STDDEV(TIMELEN_SUM)/AVG(TIMELEN_SUM)),4) as TIMELEN_SD2_MONTH," +
      "round(SUM(myDecode(TIMELEN_SUM,0,0,TIMELEN_SUM*LOG(EXP(1),TIMELEN_SUM))),4) AS TIMELEN_ENTROPY_MONTH," +
      //    --月下行流量——计算
      "round(SUM(OUTOCTETS_SUM),4) AS OUTOCTETS_SUM_MONTH," +
      "round(AVG(OUTOCTETS_SUM),4) AS OUTOCTETS_MEAN_MONTH," +
      "round(percentile(OUTOCTETS_SUM,0.5),4) AS OUTOCTETS_MEDIAN_MONTH," +
      "round(myDecode(AVG(OUTOCTETS_SUM),0,0,STDDEV(OUTOCTETS_SUM)/AVG(OUTOCTETS_SUM)),4) as OUTOCTETS_SD2_MONTH," +
      "round(SUM(myDecode(OUTOCTETS_SUM,0,0,OUTOCTETS_SUM*LOG(EXP(1),OUTOCTETS_SUM))),4) AS OUTOCTETS_ENTROPY_MONTH," +
      //    --月上行流量——计算
      "round(SUM(INOCTETS_SUM),4) AS INOCTETS_SUM_MONTH," +
      "round(AVG(INOCTETS_SUM),4) AS INOCTETS_MEAN_MONTH," +
      "round(percentile(INOCTETS_SUM,0.5),4) AS INOCTETS_MEDIAN_MONTH," +
      "round(myDecode(AVG(INOCTETS_SUM),0,0,STDDEV(INOCTETS_SUM)/AVG(INOCTETS_SUM)),4) as INOCTETS_SD2_MONTH," +
      "round(SUM(myDecode(INOCTETS_SUM,0,0,INOCTETS_SUM*LOG(EXP(1),INOCTETS_SUM))),4) AS INOCTETS_ENTROPY_MONTH," +
      //    --月下行包数——计算
      "round(SUM(OUTPACKETS_SUM),4) AS OUTPACKETS_SUM_MONTH," +
      "round(AVG(OUTPACKETS_SUM),4) AS OUTPACKETS_MEAN_MONTH," +
      "round(percentile(OUTPACKETS_SUM,0.5),4) AS OUTPACKETS_MEDIAN_MONTH," +
      "round(myDecode(AVG(OUTPACKETS_SUM),0,0,STDDEV(OUTPACKETS_SUM)/AVG(OUTPACKETS_SUM)),4) as OUTPACKETS_SD2_MONTH," +
      "round(SUM(myDecode(OUTPACKETS_SUM,0,0,OUTPACKETS_SUM*LOG(EXP(1),OUTPACKETS_SUM))),4) AS OUTPACKETS_ENTROPY_MONTH," +
      //    --月上行包数——计算
      "round(SUM(INPACKETS_SUM),4) AS INPACKETS_SUM_MONTH," +
      "round(AVG(INPACKETS_SUM),4) AS INPACKETS_MEAN_MONTH," +
      "round(percentile(INPACKETS_SUM,0.5),4) AS INPACKETS_MEDIAN_MONTH," +
      "round(myDecode(AVG(INPACKETS_SUM),0,0,STDDEV(INPACKETS_SUM)/AVG(INPACKETS_SUM)),4) as INPACKETS_SD2_MONTH," +
      "round(SUM(myDecode(INPACKETS_SUM,0,0,INPACKETS_SUM*LOG(EXP(1),INPACKETS_SUM))),4) AS INPACKETS_ENTROPY_MONTH" +
      " FROM monthData group by USERNAME,AREANO"
    val featureMonthData = spark.sql(select_month_sql)
    //    featureMonthData.show()
    featureMonthData
  }

  /**
   * 根据文件格式加载数据，参数:上级目录 月份/周列表
   */
  def loadDatas(parent: String, city: String, cycle: ArrayBuffer[String], format: String = "parquet",
                header: String = "true", inferSchema: String = "false") = {
    if (null != cycle && !"".equals(city)) {
      var i = 0
      var data = spark.read
        .format(format)
        .option("header", header)
        //      .option("inferSchema", inferSchema)
        .load(parent + "/" + cycle(i) + "/" + city)
      for (i <- 1 to cycle.length - 1) {
        data = data.union(
          spark.read
            .format(format)
            .option("header", header)
            //          .option("inferSchema", inferSchema)
            .load(parent + "/" + cycle(i) + "/" + city))
      }
      //      data.show()
      //      LOG.info("Load datas completed(数据加载完成):" + data.count())
      data
    } else {
      var i = 0
      var data = spark.read
        .format(format)
        .option("header", header)
        //      .option("inferSchema", inferSchema)
        .load(parent + "/" + cycle(i))
      for (i <- 1 to cycle.length - 1) {
        data = data.union(
          spark.read
            .format(format)
            .option("header", header)
            //          .option("inferSchema", inferSchema)
            .load(parent + "/" + cycle(i)))
      }
      //      data.show()
      //      LOG.info("Load datas completed(数据加载完成):" + data.count())
      data
    }
  }

  /**
   * 根据上月话单表月合计表中计算未使用时长
   */
  def countUnusedTime(monthFirstDay: String, city: String, cycle: ArrayBuffer[String]) = {
    LOG.info("Start count unusedTime feature(开始计算用户上月未使用时长特征).")
    loadDatas(FeaturesConf.monthStatisticPath, city, ArrayBuffer(cycle(0))).createOrReplaceTempView("beforeMonthData")
    val strSql = "SELECT USERNAME,AREANO," +
      " (datediff('" + monthFirstDay + "',max(cast(LAST_USE_TIME as String)))*24) as UNUSE_TIME" +
      " FROM beforeMonthData GROUP BY USERNAME,AREANO"
    spark.sql(strSql)
  }
  /**
   * 根据话单表以及开通历史表计算当月用户状态
   */
  def countCurruntStatus(featuresData: DataFrame, city: String, cycle: ArrayBuffer[String]) = {
    LOG.info("Start count user's status(开始计算用户状态).")
    val allUser = featuresData.select("USERNAME") //所有用户
    val wholeUser = spark.sql("select USERNAME from (select distinct USERNAME,COLLECT_CYCLE from monthData) group by USERNAME having count(*)>=" + cycle.length) //计算周期内每月都有话单的用户
    val openUser = loadDatas(FeaturesConf.userhisPath, city, ArrayBuffer(cycle(cycle.length - 1)), "csv", "true", "true")
      .filter("OTYPE = 1")
      .selectExpr("USERNAME as USERNAME") //计算周期内第一个月新开用户
    val cancelUser = loadDatas(FeaturesConf.userhisPath, city, ArrayBuffer(cycle(0)), "csv", "true", "true")
      .filter("OTYPE = 3")
      .selectExpr("USERNAME as USERNAME") //计算周期内最后一个月销户用户
    val incomplete = allUser.except(wholeUser).selectExpr("USERNAME", "-1 AS STATUS", "-1 AS STATUS_M") //话单不完整用户(所有用户-话单完整用户)：status=-1，status_m=-1
    val open = wholeUser.intersect(openUser).selectExpr("USERNAME", "-1 AS STATUS", "-2 AS STATUS_M") //话单完整并且为第一个月新开用户:status=-1，status_m=-2
    val cancel = wholeUser.intersect(cancelUser).selectExpr("USERNAME", "-1 AS STATUS", "-3 AS STATUS_M") //话单完整并且为最后一个月销户用户:status=-1，status_m=-3
    val complete = wholeUser.except(openUser.union(cancelUser)).selectExpr("USERNAME", "0 AS STATUS", "0 AS STATUS_M") //话单完整并且非新开或销户用户(话单完整用户-(新开用户+销户用户))status=0，status_m=0
    incomplete.union(open).union(cancel).union(complete).toDF("USERNAME", "STATUS", "STATUS_M")
  }

  /**
   * 根据未使用时长计算健康值
   */
  def calcHealthVal(featuresData: DataFrame, startDay: String) = {
    LOG.info("Start count user's status(开始计算用户健康值).")
    featuresData.createOrReplaceTempView("calcHealthValTable")
    featuresData.show()
    val strSql = "SELECT USERNAME,AREANO," +
      "CASE WHEN UNUSE_TIME > 0 AND UNUSE_TIME <= 120  THEN 90 WHEN UNUSE_TIME > 120 AND UNUSE_TIME < 240  THEN 60 WHEN UNUSE_TIME > 240 AND UNUSE_TIME < monthHour(" + startDay + ") THEN 40 WHEN UNUSE_TIME = monthHour(" + startDay + ") THEN 0 ELSE 100 END as HEALTH_VAL" +
      " FROM calcHealthValTable"
    spark.sql(strSql)
  }

  def main(args: Array[String]): Unit = {
    var cityValue = "no"
    var startDay = TimeTool.month()
    var count = 3
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