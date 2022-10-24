package com.ainsg.lwfx.clean.service
/**
 * 数据清洗主函数
 */

import com.ainsg.lwfx.clean.config.CleanConf
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ainsg.lwfx.config.SparkConf

object CleanService {
  val LOG: Logger = LoggerFactory.getLogger(CleanService.getClass)
  /**
   * 数据清洗
   */
  def clean(): Unit = {
    val cleanBusiness = CleanConf.getProp("clean.business")
    val arrayBusiness = cleanBusiness.split(",")
    var business = ""
    var name, localDir, newFileDir, isCity, field, table, selectField, filterCondition, fileSuffix, months, saveMode, dateFormateValue, cityCode, regionNum, split, format = ""
    val threadPool: ExecutorService = Executors.newFixedThreadPool(arrayBusiness.length)
    try {
      for (business <- arrayBusiness) {
        name = CleanConf.getProp("clean.business." + business + ".name")
        localDir = SparkConf.hadoopMaster + CleanConf.getProp("clean.business." + business + ".localDir")
        newFileDir = SparkConf.hadoopMaster + CleanConf.getProp("clean.business." + business + ".newFileDir")
        isCity = CleanConf.getProp("clean.business." + business + ".isCity")
        field = CleanConf.getProp("clean.business." + business + ".field")
        table = CleanConf.getProp("clean.business." + business + ".table")
        selectField = CleanConf.getProp("clean.business." + business + ".selectField")
        filterCondition = CleanConf.getProp("clean.business." + business + ".filterCondition")
        fileSuffix = CleanConf.getProp("clean.business." + business + ".fileSuffix")
        months = CleanConf.getProp("clean.business." + business + ".months")
        saveMode = CleanConf.getProp("clean.business." + business + ".saveMode")
        split = CleanConf.getProp("clean.business." + business + ".split")
        cityCode = CleanConf.getProp("clean.business." + business + ".cityCode")
        regionNum = CleanConf.getProp("clean.business." + business + ".region_num")
        format = CleanConf.getProp("clean.business." + business + ".format")
        LOG.info("[name:" + name + ", localDir:" + localDir + ", newFileDir:" + newFileDir + ", isCity:" + isCity
          + ", field:" + field + ", table:" + table + ", selectField:" + selectField + ", filterCondition:" + filterCondition
          + ", fileSuffix:" + fileSuffix + ", months:" + months + ", saveMode:" + saveMode + ", cityCode:" + cityCode + ", regionNum:" + regionNum)
        var cleanConfig = new CleanConfig(name, localDir, newFileDir, isCity, field, table, selectField, filterCondition, saveMode, fileSuffix, months, split, cityCode, regionNum, format)
        threadPool.execute(new CleanRunnable(cleanConfig, LOG))
      }
    } finally {
      threadPool.shutdown()
    }
  }

  def main(args: Array[String]): Unit = {
    clean()
  }
}