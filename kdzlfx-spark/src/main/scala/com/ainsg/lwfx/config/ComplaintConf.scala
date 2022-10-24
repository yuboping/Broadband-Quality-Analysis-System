package com.ainsg.lwfx.config

import com.ainsg.lwfx.util.LoadFiles

/**
 * 特征值计算相关配置
 */
object ComplaintConf {
  
  val file: LoadFiles = new LoadFiles("complaint.properties")
  val hadoopMaster = file.getProp("hadoop.master")
  val usageComplaintPath = SparkConf.hadoopMaster + file.getProp("usageComplaint.path")
  val provHistoryPath = SparkConf.hadoopMaster + file.getProp("provHistory.path")
  val monthStatisticPath = SparkConf.hadoopMaster + file.getProp("monthStatistic.path")
  val weekStatisticPath = SparkConf.hadoopMaster + file.getProp("weekStatistic.path")
  val featurePath = SparkConf.hadoopMaster + file.getProp("feature.path")
  val crmpath = SparkConf.hadoopMaster + file.getProp("crm.path")
  val spark_tagcountTask = file.getProp("spark.tagcountTask.scope")
  val spark_sql_dir = file.getProp("spark.sql.warehouse.dir")
  val spark_name = file.getProp("spark.name")
  val totalType = file.getProp("totalType")
  
}