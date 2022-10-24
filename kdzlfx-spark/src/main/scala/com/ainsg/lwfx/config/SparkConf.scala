package com.ainsg.lwfx.config
import com.ainsg.lwfx.util.LoadFiles
/**
 * spark、hdfs 集群相关配置
 */
object SparkConf {
  val file: LoadFiles = new LoadFiles("spark.properties")
  /**
   * spark 配置
   */
  val spark_tagcountTask = file.getProp("spark.tagcountTask.scope")
  val spark_sql_dir = file.getProp("spark.sql.warehouse.dir")
  val spark_name = file.getProp("spark.name")
  
  /** hadoop.master 配置 */
  val hadoopMaster = file.getProp("hadoop.master")
}