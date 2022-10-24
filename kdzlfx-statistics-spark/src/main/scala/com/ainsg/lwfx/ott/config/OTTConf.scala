package com.ainsg.lwfx.ott.config

import com.ainsg.lwfx.util.LoadFiles
/**
 * 特征值计算相关配置
 */
object OTTConf {
  val file: LoadFiles = new LoadFiles("ott.properties")
  val hadoopMaster = file.getProp("hadoop.master")
  val usageOTTPath = hadoopMaster + file.getProp("usageOTT.path")
  val monthStatisticPath = hadoopMaster + file.getProp("monthStatistic.path")
  val featurePath = hadoopMaster + file.getProp("feature.path")
  val spark_tagcountTask = file.getProp("spark.tagcountTask.scope")
  val spark_sql_dir = file.getProp("spark.sql.warehouse.dir")
  val spark_name = file.getProp("spark.name")
  val totalType = file.getProp("totalType")
  val ottThreshold = file.getProp("ott.threshold")
  val tempPath = file.getProp("temp.path")
}