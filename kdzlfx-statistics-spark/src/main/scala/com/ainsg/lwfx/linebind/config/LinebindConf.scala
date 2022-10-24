package com.ainsg.lwfx.linebind.config
import com.ainsg.lwfx.util.LoadFiles
/**
 * 特征值计算相关配置
 */
object LinebindConf {
  val file: LoadFiles = new LoadFiles("linebind.properties")
  val hadoopMaster = file.getProp("hadoop.master")
  val userdetPath = file.getProp("userdet.path")
  val linebindMonthStatisticPath = file.getProp("linebindMonthStatistic.path")
  val linebindFeaturePath = file.getProp("linebindFeature.path")
  val spark_tagcountTask = file.getProp("spark.tagcountTask.scope")
  val spark_sql_dir = file.getProp("spark.sql.warehouse.dir")
  val spark_name = file.getProp("spark.name")
  val totalType = file.getProp("totalType")

}