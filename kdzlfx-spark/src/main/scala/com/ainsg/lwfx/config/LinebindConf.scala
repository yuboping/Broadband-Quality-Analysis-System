package com.ainsg.lwfx.config
import com.ainsg.lwfx.util.LoadFiles
import com.ainsg.lwfx.clean.config.CleanConf
/**
 * 特征值计算相关配置
 */
object LinebindConf {
  val file: LoadFiles = new LoadFiles("linebind.properties")
  val userdetName = file.getProp("clean.name.userdet")
  val userdetPath = SparkConf.hadoopMaster + CleanConf.getProp("clean.business." + userdetName + ".newFileDir")
  val linebindMonthStatisticPath = SparkConf.hadoopMaster + file.getProp("linebindMonthStatistic.path")
  val linebindFeaturePath = SparkConf.hadoopMaster + file.getProp("linebindFeature.path")
  val totalType = file.getProp("totalType")
}