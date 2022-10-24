package com.ainsg.lwfx.service
import scala.collection.mutable.ArrayBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SaveMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.ainsg.lwfx.common.Spark.spark
import com.ainsg.lwfx.config.LinebindConf
import com.ainsg.lwfx.util.JdbcOracleHelper
import com.ainsg.lwfx.util.TimeTool

/**
 * 特征计算
 */
object LinebindService {
  val LOG: Logger = LoggerFactory.getLogger(LinebindService.getClass)
  /**
   * 计算用户特征值以及用户初始状态，同时更新上月特征表用户状态
   * 地市取值 cityValue : all 全部地市，01,02 部分地市，no 不存在地市,默认 no
   * 统计方式 totalType：合并类型（month=按月，week=按周），默认按月
   * 统计周期数 count：合计开始日期往前N个月/周，默认为3
   * startDay：开始日期（按月则用yyyymm格式，按周则为yyyyww格式），默认为当前月
   */
  def featureStatistic(city: String, count: Int, startDay: String): Unit = {
    if ("month".equals(LinebindConf.totalType)) {
      val cycle: ArrayBuffer[String] = TimeTool.PreNmonth(startDay, count)
      LOG.info("Computing cycle is(计算周期):" + cycle)
      val citys = LinebindStatisticService.getCityArray(cycle(0), city, LinebindConf.userdetPath)
      LOG.info("Computing city is(计算地市):" + citys)
      if (citys.isEmpty) {
        featureStatisticByCity("", startDay, cycle)
      } else {
        citys.foreach(city => featureStatisticByCity(city, startDay, cycle))
      }
    }
  }
  /**
   * 按地市对用户进行特征值计算
   */
  def featureStatisticByCity(city: String, startDay: String, cycle: ArrayBuffer[String]) = {
    chekMonthData(city, cycle)
    saveCurruntLinebind(city, startDay, cycle)
  }

  /**
   * 检查月合计数据是否存在，不存在则进行月数据合计
   */
  def chekMonthData(city: String, cycle: ArrayBuffer[String]) = {
    LOG.info("Check total datas is exist(检查合计数据是否存在).")
    cycle.foreach(month => {
      //      val files = HadoopTool.getFileNameABFromPath(
      //        LinebindConf.linebindMonthStatisticPath + "/" + month + "/" + city)
      //      if (files.length == 0) {
      LOG.info(LinebindConf.linebindMonthStatisticPath + "/" + month + "/" + city + " has no files.")
      LinebindStatisticService.totalData(city, 1, month)
      //      }
    })
  }
  /**
   * 计算当月用户特征及可预测状态
   */
  def saveCurruntLinebind(city: String, startDay: String, cycle: ArrayBuffer[String]) = {
    val monthFirstDay = TimeTool.monthFirstDay(startDay, "yyyyMM")
    var linebindData = loadDetailDatas(city, cycle)
//    LOG.info("final feature is(最终用户特征数据):" + linebindData.count())
//    linebindData.show()
    if (city == "") {
      linebindData.write.option("header", true).mode(SaveMode.Overwrite).format("csv").save(LinebindConf.linebindFeaturePath + "/" + startDay)
    } else {
      linebindData.write.option("header", true).mode(SaveMode.Overwrite).format("csv").save(LinebindConf.linebindFeaturePath + "/" + startDay + "/" + city)
    }
    val month = startDay.substring(4, 6)
    val sql = "INSERT INTO CES_LINE_CHARACTER_" + month + " (USER_NAME,CITY_CODE,BRASIP,SLOT,SUBSLOT,PORT,VPI,VCI,VLAN,SVLAN,ANID,ANIRACK,ANIFRAME,ANISLOT,ANISUBSLOT,ANIPORT,ONUID,PON) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
    JdbcOracleHelper.truncateTable("CES_LINE_CHARACTER_" + month)
    JdbcOracleHelper.insertWithRDD(linebindData, sql)
  }

  /**
   * 加载号线特征
   */
  def loadDetailDatas(city: String, cycle: ArrayBuffer[String]): DataFrame = {
    LOG.info("Start count detail feature(开始计算用户号线特征).")
    loadDatas(LinebindConf.linebindMonthStatisticPath, city, cycle).createOrReplaceTempView("monthData")
    var select_month_sql: String = "select * from ( select USERNAME,AREANO,NASIP,SLOT,SUBSLOT,PORT,VPI,VCI,VLAN,SVLAN,ANID,ANIRACK,ANIFRAME,ANISLOT,ANISUBSLOT,ANIPORT,ONUID,PON,row_number() over(partition by USERNAME,AREANO order by STOPTIME desc) as RANK from monthData) WHERE RANK = '1'"
    val featureMonthData = spark.sql(select_month_sql).drop("RANK")
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