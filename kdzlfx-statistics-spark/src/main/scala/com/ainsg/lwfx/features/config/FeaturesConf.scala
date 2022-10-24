package com.ainsg.lwfx.features.config
import com.ainsg.lwfx.util.LoadFiles
/**
 * 特征值计算相关配置
 */
object FeaturesConf {
  val file: LoadFiles = new LoadFiles("features.properties")
  val hadoopMaster = file.getProp("hadoop.master")
  val userdetPath = file.getProp("userdet.path")
  val userhisPath = file.getProp("userhis.path")
  val monthStatisticPath = file.getProp("monthStatistic.path")
  val weekStatisticPath = file.getProp("weekStatistic.path")
  val featurePath = file.getProp("feature.path")
  val crmpath = file.getProp("crm.path")
  val spark_tagcountTask = file.getProp("spark.tagcountTask.scope")
  val spark_sql_dir = file.getProp("spark.sql.warehouse.dir")
  val spark_name = file.getProp("spark.name")
  val totalType = file.getProp("totalType")
  
}