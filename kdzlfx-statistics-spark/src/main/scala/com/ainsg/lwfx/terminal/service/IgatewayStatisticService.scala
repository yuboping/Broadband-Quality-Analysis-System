package com.ainsg.lwfx.terminal.service
import com.ainsg.lwfx.util.{ HadoopTool, TimeTool }
import org.slf4j.{ Logger, LoggerFactory };
import scala.collection.mutable.ArrayBuffer
import com.ainsg.lwfx.common.Spark._
import com.ainsg.lwfx.common.Spark.spark.implicits._
import com.ainsg.lwfx.terminal.config.TerminalConf
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.SaveMode
import org.apache.hadoop.fs.FileStatus
import com.ainsg.lwfx.util.ArrayTool
import com.ainsg.lwfx.util.FileUtil

/**
 * 数据合计（天合成周/月）并保存合计文件
 */

case class IgatewayString(
  USERNAME:              String,
  MACK:                  String,
  STOPTIME:              String,
  var INTELIGENTGATEWAY: String,
  var SUBDEVICES:        String,
  AREANO:                String)
object IgatewayStatisticService {
  val LOG: Logger = LoggerFactory.getLogger(IgatewayStatisticService.getClass)
  /**
   * 将按天采集到的数据进行合并计算
   * 地市取值 cityValue : all 全部地市，01,02 部分地市，no 不存在地市,默认 no
   * 统计方式 totalType：合并类型（month=按月，week=按周），默认按月
   * 统计周期数 count：合计开始日期往前N个月/周，默认为3
   * startDay：开始日期（按月则用yyyymm格式，按周则为yyyyww格式），默认为当前月
   */
  def totalData(cityValue: String, count: Int, startDay: String): Unit = {
    if ("month".equals(TerminalConf.igatewayTotalType)) {
      LOG.info("Statistic month(统计月份):" + startDay)
      monthStatistics(cityValue, startDay)
    } else if ("week".equals(TerminalConf.igatewayTotalType)) { //待实现

    }

  }

  /**
   * 计算month 月份下 cityCode 值，返回字符串数组["01","02","03"]
   */
  def getCityArray(month: String, cityValue: String, basePath: String): ArrayBuffer[String] = {
    var result = new ArrayBuffer[String]()
    if ("".equals(cityValue) || null == cityValue || "no".equals(cityValue)) {

    } else if ("all".equals(cityValue)) {
      //      FeaturesConf.userdetPath + "/" + month 下 子目录
      result = FileUtil.getDirNameFromPath(basePath + "/" + month)
    } else {
      val citys = cityValue.split(",")
      citys.foreach(citycode => result.append(citycode))
    }
    result
  }

  /**
   * 按月统计
   */
  def monthStatistics(cityValue: String, month: String): Unit = {
    val citylist = getCityArray(month, cityValue, TerminalConf.userdetPath);
    if (citylist.length == 0) {
      LOG.info("cityValue is empty")
      statisticsByCity(month, null)
    } else {
      LOG.info("cityValue is(统计地市):" + citylist.toString())
      citylist.foreach(cityCode => statisticsByCity(month, cityCode))
    }
  }

  /**
   * sourceDir 数据源路径
   * viewName 表名称
   * cityCodeField 省市字段
   * cityDir 省市数据存放路径
   */
  def statisticsByCity(month: String, cityCode: String): Unit = {
    var sourceDir = TerminalConf.userdetPath + "/" + month
    var viewName = "igateway"
    var cityCodeField = "AREANO"
    var cityDir = ""
    if (null != cityCode && !"".equals(cityCode)) {
      sourceDir = TerminalConf.userdetPath + "/" + month + "/" + cityCode
      cityCodeField = "AREANO"
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

    var usageBPPP = spark.read.format("csv")
      .option("header", "true") //csv第一行有属性的话"true"，没有就是"false"
      .option("inferSchema", false.toString) //自动推断属性列的数据类型。
      .load(sourceDir)
    var data = usageBPPP.selectExpr("USERNAME", "MACK", "STOPTIME", "0 as INTELIGENTGATEWAY", "0 as SUBDEVICES", cityCodeField)
    data.createOrReplaceTempView(viewName)
    //    LOG.info("IgatewayStatisticService:Total data datas:" + data.count())
    //    val usageBPPP = spark.read.format("csv")
    //      .option("header", "true") //csv第一行有属性的话"true"，没有就是"false"
    //      .load(sourceDir)
    //      .selectExpr("USERNAME", "MACK", "STOPTIME", "0 as INTELIGENTGATEWAY", "0 as SUBDEVICES", cityCodeField)
    //      .as[IgatewayString]
    //      .map(usage => {
    //        usage.SUBDEVICES = "0"
    //        usage.INTELIGENTGATEWAY = "0"
    //        usage
    //      })
    //    usageBPPP.createOrReplaceTempView(viewName);
    viewName
  }
  /**
   * 合计月数据
   */
  def saveMonth(viewName: String, month: String, cityDir: String, cityCodeField: String) = {
    LOG.info("Total month datas(合计月数据).")
    var dataMonthSql = "select * from ( select USERNAME," + cityCodeField + ",STOPTIME,INTELIGENTGATEWAY,SUBDEVICES,MACK,row_number() over(partition by USERNAME,AREANO order by STOPTIME desc) as RANK from " + viewName + ") WHERE RANK = '1'"
    val dataMonth = spark.sql(dataMonthSql).drop("RANK")
    //    HadoopTool.mkDir(TerminalConf.monthStatisticPath, month + cityDir)
    //    LOG.info("datas completed(合计月数据完成):" + dataMonth.count())
    //    dataMonth.show()
    //    LOG.info("IgatewayStatisticService:Total month datas completed(合计月数据完成):" + dataMonth.count())
    if (null != cityDir && !"".equals(cityDir)) {
      dataMonth.write.mode(SaveMode.Overwrite).save(TerminalConf.igatewayMonthStatisticPath + "/" + month + cityDir)
    } else {
      dataMonth.write.mode(SaveMode.Overwrite).save(TerminalConf.igatewayMonthStatisticPath + "/" + month)
    }
    //    LOG.info("Total month datas completed(合计月数据完成):" + dataMonth.count())
  }

  def main(args: Array[String]): Unit = {
    var cityValue = "no"
    var count = 3
    var startDay = TimeTool.month()

    if (args.length >= 3 && args(2).length == 6) {
      startDay = args(2)
    }

    if (args.length >= 2) {
      count = Integer.parseInt(args(1))
    }
    if (args.length >= 1) {
      cityValue = args(0)
    }
    LOG.info("Start data statistics(数据统计开始).")
    LOG.info("Running params is(运行参数为):cityValue=" + cityValue + ",count=" + count + ",startDay=" + startDay)
    totalData(cityValue, count, startDay)
    LOG.info("End data statistics(数据统计结束).")
  }
}