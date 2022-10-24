package com.ainsg.lwfx.clean.service

import com.ainsg.lwfx.common.Spark.spark
import com.ainsg.lwfx.common.Spark.spark.implicits._
import com.ainsg.lwfx.transformer.DataColumns
import com.ainsg.lwfx.util.TimeTool
import com.ainsg.lwfx.util.HadoopTool
import org.apache.spark.sql.{ SaveMode, DataFrame, Row, SparkSession, Column }
import com.ainsg.lwfx.clean.config.CleanConf
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ainsg.lwfx.util.FileUtil
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

class CleanRunnable(cleanConfig: CleanConfig, LOG: Logger) extends Runnable {

  override def run() {
    LOG.info("CleanRunnable Start name is : " + cleanConfig.name)
    /**
     * 新建临时目录，将localDir已完成数据转移到 临时目录
     * 从localDir 目录中复杂 文件名后缀符合 fileSuffix 的文件到 临时文件 localTempDir
     */
    val localTempDir = cleanConfig.localDir + "/tmp"
    //备份目录
    val backupDir = cleanConfig.localDir + "/backup"
    FileUtil.moveFile(cleanConfig.localDir, localTempDir, cleanConfig.fileSuffix)
    //    HadoopTool.moveFile(cleanConfig.localDir, localTempDir, cleanConfig.fileSuffix)
    /**
     * spark 读取 localTempDir 中文件，操作完成后删除 localTempDir 中数据文件
     * cleanConfig.field 字段数，去除不合格数据
     */
    val filedNum: Int = cleanConfig.field.count(_ == ',')
    val split = cleanConfig.split.charAt(0)
    val format = cleanConfig.format
    var data: DataFrame = null
    if(format.equals("csv")) {
      var schemaString = cleanConfig.field
      var columnArray = schemaString.split(",")
      var fields = columnArray.map(fieldName => StructField(fieldName, StringType, nullable = true))
      var schema = StructType(fields)
      data = spark.read.format("csv")
            .option("header", "false") //csv第一行有属性的话"true"，没有就是"false"
            .schema(schema)
            .load(localTempDir)
    } else {
      val rddata = spark.read.format("text").load(localTempDir).as[String]
      //将rdd转换成dataframe,用spark sql 操作
      data = DataColumns.RDDToDF(spark, rddata.rdd, cleanConfig.field, cleanConfig.regionNum, cleanConfig.split)
    }
//    LOG.info(cleanConfig.name + "---data.count=" + data.count())
//    data.show()
    // 创建视图
    data.createTempView(cleanConfig.table)
    // isCity 0: 按/201801 目录存放数据  1：按 /201801/city/ 格式存放数据
    if (cleanConfig.isCity == 0) {
      handleDataByMonth(cleanConfig, data)
    } else if (cleanConfig.isCity == 1) {
      handleDataByCity(cleanConfig, data)
    }
    //文件备份至bak目录
    FileUtil.moveFile(localTempDir, backupDir, cleanConfig.fileSuffix)
    spark.catalog.dropTempView(cleanConfig.table)
    LOG.info("CleanRunnable End name is : " + cleanConfig.name)
  }

  //按月份分目录
  def handleDataByMonth(cleanConfig: CleanConfig, data: DataFrame): Unit = {
    val monthArray = TimeTool.getMonths(cleanConfig.months)
    for (month <- monthArray) {
      var sql = "select " + cleanConfig.selectField + " from " + cleanConfig.table + " where 1=1 "
      if (null != cleanConfig.filterCondition && !"".equals(cleanConfig.filterCondition)) {
        sql = sql + " and " + cleanConfig.filterCondition.replaceAll("#month", "'" + month + "'")
      }
      LOG.info(cleanConfig.name + "---sql:" + sql)
      val monthData = spark.sql(sql)
      val ct: Long = monthData.count()
      LOG.info(cleanConfig.name + "---ct:" + ct)
      if (ct > 0) {
//        monthData.show()
        LOG.info(cleanConfig.name + "---newFileDir:" + cleanConfig.newFileDir + "/" + month)
        monthData.write.option("header", true).mode(SaveMode.Append).csv(cleanConfig.newFileDir + "/" + month)
      }
    }
  }

  //按月份/城市编码 分目录
  def handleDataByCity(cleanConfig: CleanConfig, data: DataFrame): Unit = {
    val monthArray = TimeTool.getMonths(cleanConfig.months)
    val cityCodes = CleanConf.getProp("clean.city.codes")
    val cityCodelist = cityCodes.split(",")
    for (month <- monthArray) {
      //先判断当前月份下是否存在数据，存在执行城市操作
      var sql = "select " + cleanConfig.selectField + " from " + cleanConfig.table + " where 1=1 "
      if (cleanConfig.filterCondition != null && !"".equals(cleanConfig.filterCondition)) {
        sql = sql + " and " + cleanConfig.filterCondition.replaceAll("#month", "'" + month + "'")
      }
      LOG.info(cleanConfig.name + "---当前月" + month + "查询sql:" + sql)
      val monthData = spark.sql(sql)
      val ct: Long = monthData.count()
      LOG.info(cleanConfig.name + "---当前月" + month + "查询数量: {}", ct)
      for (citycode <- cityCodelist) {
        var cityDataSql: String = "select " + cleanConfig.selectField + " from " + cleanConfig.table + " where 1=1 "
        if (null != cleanConfig.filterCondition && !"".equals(cleanConfig.filterCondition)) {
          cityDataSql = cityDataSql + " and " + cleanConfig.filterCondition.replaceAll("#month", "'" + month + "'") + " and " + cleanConfig.cityCode + "='" + citycode + "'"
        }
        if (null != cleanConfig.cityCode && !"".equals(cleanConfig.cityCode)) {
          cityDataSql = cityDataSql + " and " + cleanConfig.cityCode + "='" + citycode + "'"
        }
        val cityData = spark.sql(cityDataSql)
        val city_ct: Long = cityData.count()
        LOG.info(cleanConfig.name + "---当前月" + month + "下城市编码为 " + citycode + " 查询数量: {}", city_ct)
        if (city_ct > 0) {
//          cityData.show()
          if (cleanConfig.saveMode.equals("Append")) {
            cityData.write.option("header", true).mode(SaveMode.Append).csv(cleanConfig.newFileDir + "/" + month + "/" + citycode)
          } else {
            cityData.write.option("header", true).mode(SaveMode.Overwrite).csv(cleanConfig.newFileDir + "/" + month + "/" + citycode)
          }
        }
      }
    }
  }

}