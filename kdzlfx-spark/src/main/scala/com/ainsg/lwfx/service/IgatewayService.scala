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
import org.apache.spark.storage.StorageLevel

/**
 * 特征计算
 */
object IgatewayService {
  val LOG: Logger = LoggerFactory.getLogger(IgatewayService.getClass)
  /**
   * 计算用户特征值以及用户初始状态，同时更新上月特征表用户状态
   * 地市取值 cityValue : all 全部地市，01,02 部分地市，no 不存在地市,默认 no
   * 统计方式 totalType：合并类型（month=按月，week=按周），默认按月
   * 统计周期数 count：合计开始日期往前N个月/周，默认为3
   * startDay：开始日期（按月则用yyyymm格式，按周则为yyyyww格式），默认为当前月
   */
  def featureStatistic(city: String, count: Int, startDay: String): Unit = {
    if ("month".equals(TerminalConf.igatewayTotalType)) {
      val cycle: ArrayBuffer[String] = TimeTool.PreNmonth(startDay, count)
      LOG.info("Computing cycle is(计算周期):" + cycle)
      val citys = IgatewayStatisticService.getCityArray(cycle(0), city, TerminalConf.userdetPath)
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
    /**
     * 数据已跑完，不要重复跑
     */
    chekMonthData(city, cycle)
    saveCurruntIgateway(city, startDay, cycle)
  }

  /**
   * 检查月合计数据是否存在，不存在则进行月数据合计
   */
  def chekMonthData(city: String, cycle: ArrayBuffer[String]) = {
    LOG.info("Check total datas is exist(检查合计数据是否存在).")
    cycle.foreach(month => {
      //      val files = HadoopTool.getFileNameABFromPath(
      //        TerminalConf.igatewayMonthStatisticPath + "/" + month + "/" + city)
      //      if (files.length == 0) {
      LOG.info(TerminalConf.igatewayMonthStatisticPath + "/" + month + "/" + city + " has no files.")
      IgatewayStatisticService.totalData(city, 1, month)
      //      }
    })
  }
  /**
   * 计算当月用户特征及可预测状态
   */
  def saveCurruntIgateway(city: String, startDay: String, cycle: ArrayBuffer[String]) = {
    val monthFirstDay = TimeTool.monthFirstDay(startDay, "yyyyMM")
    var igatewayData = loadDetailDatas(city, cycle)
    //    igatewayData.persist()
    //缓存入磁盘
    //    igatewayData.persist(StorageLevel.DISK_ONLY)
    //    LOG.info("final feature is(最终用户特征数据):" + igatewayData.count())
    //    igatewayData.show()
    if (city == "") {
      igatewayData.write.option("header", true).mode(SaveMode.Overwrite)
        .format("csv").save(TerminalConf.igatewayFeaturePath + "/" + startDay)
    } else {
      igatewayData.write.option("header", true).mode(SaveMode.Overwrite).format("csv").save(TerminalConf.igatewayFeaturePath + "/" + startDay + "/" + city)
    }
    //清除缓存
    //    igatewayData.unpersist()
  }

  /**
   * 加载智能网关特征(根据智能网关以及智能网关下挂设备计算当月智能网关个数和智能网关下挂设备个数)
   */
  def loadDetailDatas(city: String, cycle: ArrayBuffer[String]): DataFrame = {
    LOG.info("Start count detail feature(开始计算用户智能网关特征).")
    // 加载数据--网关数据
    val igatewagMonthData = loadDatas(TerminalConf.igatewayMonthStatisticPath, city, cycle).filter("length(regexp_replace(mack,':','')) = 12")
    igatewagMonthData.createOrReplaceTempView("monthData")

    //    LOG.info("IgatewayStatisticService:igatewagMonthData datas:" + igatewagMonthData.count())
    // 数据清洗 MACK去除":",前6位一致，后6位：从16进制转换成10进制并减1,然后再转换成16进制。
    var select_month_sql: String = "select t.USERNAME,t.AREANO,t.MACK,t.MACK_T from " +
      "( select USERNAME,AREANO,MACK," +
      "concat(SUBSTR(LOWER(mack), 0, 9),addColon(tenTransSixteen(sixteenTransTen(SUBSTR(LOWER(regexp_replace(mack, ':', '')), 7, 6))-1))) as MACK_T," +
      "row_number() over(partition by USERNAME,AREANO order by STOPTIME desc) as RANK from monthData) t" +
      " WHERE t.RANK = '1'"
    val featureMonthData = spark.sql(select_month_sql)
    spark.catalog.dropTempView("monthData")
    //用户MACK整理成视图
    featureMonthData.show()
    //    LOG.info("IgatewayService:featureMonthData datas:" + featureMonthData.count())
    featureMonthData.createTempView("featureMonthView")

    // 加载DHCP数据
    val dhcpData = loadDatas(TerminalConf.dhcpPath, "", cycle, "csv", "true", "true")
    dhcpData.createOrReplaceTempView("dhcpData")

    //sql语句 关联两张表(智能网关)
    var isOrNotDhcpSql = "SELECT t1.USERNAME,t1.AREANO," +
      " strNullSetVal(t2.CLIENTIDENTIFIER,'0','1') as INTELIGENTGATEWAY" +
      " FROM featureMonthView t1 left outer join dhcpData t2 " +
      " on (t1.MACK_T = LOWER(t2.CLIENTIDENTIFIER))"
    var DhcpMonthData = spark.sql(isOrNotDhcpSql)
    DhcpMonthData.createTempView("dhcpMonthView")
    spark.catalog.dropTempView("featureMonthView")
    //    LOG.info("IgatewayService:DhcpMonthData datas:" + DhcpMonthData.count())

    // igateway加载数据
    val igatewayData = loadDatas(TerminalConf.igatewayPath, "", cycle, "csv", "true", "true")
    igatewayData.createOrReplaceTempView("igatewayData")

    // igatewayData 数据清洗
    var igatewayDataCleanSql = "select USERNAME, count(*) as SUBDEVICES " +
      " from igatewayData group by USERNAME"
    var igatewayCleanData = spark.sql(igatewayDataCleanSql).select("USERNAME", "SUBDEVICES")
    //    LOG.info("IgatewayService:igatewayCleanData datas:" + igatewayCleanData.count())
    spark.catalog.dropTempView("igatewayData")
    igatewayCleanData.createTempView("igatewayDataView")
    //    igatewayCleanData.show()

    //sql语句 关联两张表(智能网关下挂设备个数)
    var joinSql = " select t1.USERNAME,t1.AREANO,t1.INTELIGENTGATEWAY," +
      "IFNULL(t2.SUBDEVICES,0) as SUBDEVICES" +
      " from dhcpMonthView t1 left outer join igatewayDataView t2" +
      " on t1.USERNAME=t2.USERNAME and t1.INTELIGENTGATEWAY!=0"
    var joinData = spark.sql(joinSql)
    //    LOG.info("IgatewayService:joinData datas:" + joinData.count())
    joinData = joinData.distinct()
    //    LOG.info("IgatewayService:joinData distinct datas:" + joinData.count())
    spark.catalog.dropTempView("igatewayView")
    joinData
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