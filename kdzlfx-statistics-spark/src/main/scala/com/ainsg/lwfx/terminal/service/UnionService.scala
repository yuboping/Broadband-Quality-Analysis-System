package com.ainsg.lwfx.terminal.service
import scala.collection.mutable.ArrayBuffer

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SaveMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.ainsg.lwfx.common.Spark.spark
import com.ainsg.lwfx.terminal.config.TerminalConf
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
    LOG.info("Start feature calculation(特征值计算开始).")
    LOG.info("Running params is(运行参数为):cityValue=" + cityValue + ",count=" + count + ",startDay=" + startDay)
    TerminalService.terminalStatistic(date, cityValue, startDay)
    IgatewayService.featureStatistic(cityValue, count, startDay)
    //    TerminalService.loadData(TerminalConf.terminalUserDir + "/" + date.substring(0, 6), "terminalData")
    var terminalData = TerminalService.loadData(TerminalConf.terminalUserDir + "/" + date.substring(0, 6), "terminalData")
    LOG.info("UnionService:terminalData datas:" + terminalData.count())
    var sql = ""
    if (null == terminalData || terminalData.rdd.isEmpty()) {
      LOG.info("terminalData is null.")
      sql = "SELECT USERNAME,AREANO,'0' as BADQUALITY, INTELIGENTGATEWAY,SUBDEVICES FROM igatewayData"
    } else {
      sql = "SELECT t1.USERNAME,t1.AREANO,calcBQ(IFNULL(t2.AUTHFAIL_FLAG,'0'),IFNULL(t2.SHORTTIME_FLAG,'0'),IFNULL(t2.OFTENDOWN_FLAG,'0')) AS BADQUALITY,t1.INTELIGENTGATEWAY,t1.SUBDEVICES," + 
        "calcHealthValue(IFNULL(t2.AUTHFAIL_FLAG,'0'),IFNULL(t2.SHORTTIME_FLAG,'0'),IFNULL(t2.OFTENDOWN_FLAG,'0')) AS HEALTH_VAL," +
        "IFNULL(t2.AUTHFAIL_FLAG,'0') AS AUTHFAIL_FLAG,IFNULL(t2.AUTHFAIL_COUNT,'0') AS AUTHFAIL_COUNT,IFNULL(t2.SHORTTIME_FLAG,'0') AS SHORTTIME_FLAG," + 
        "IFNULL(t2.SHORTTIME_COUNT,'0') AS SHORTTIME_COUNT,IFNULL(t2.OFTENDOWN_FLAG,'0') AS OFTENDOWN_FLAG,IFNULL(t2.OFTENDOWN_COUNT,'0') AS OFTENDOWN_COUNT " + 
        "FROM igatewayData t1 LEFT JOIN terminalData t2 ON t2.USERNAME = t1.USERNAME and t2.AREANO = t1.AREANO"
    }

    var igatewayDataD = TerminalService.loadData(TerminalConf.igatewayFeaturePath + "/" + startDay, "igatewayData")
    LOG.info("UnionService:igatewayDataD datas:" + igatewayDataD.count())
    var insertData = spark.sql(sql)
    LOG.info("UnionService:insertData datas:" + insertData.count())
    //    insertData.show()
    var month = startDay.substring(4, 6)
    val insertSql = "INSERT INTO CES_TERMINAL_CHARACTER_" + month + " (USER_NAME,CITY_CODE,BADQUALITY,INTELIGENTGATEWAY,SUBDEVICES,HEALTH_VAL,AUTHFAIL_FLAG,AUTHFAIL_COUNT,SHORTTIME_FLAG,SHORTTIME_COUNT,OFTENDOWN_FLAG,OFTENDOWN_COUNT) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"
    JdbcOracleHelper.truncateTable("CES_TERMINAL_CHARACTER_" + month)
    JdbcOracleHelper.insertWithRDD(insertData, insertSql)
    LOG.info("End feature calculation(特征值计算结束).")
  }
}