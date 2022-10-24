package com.ainsg.lwfx.config
import com.ainsg.lwfx.util.LoadFiles
import com.ainsg.lwfx.clean.config.CleanConf
/**
 * 特征值计算相关配置
 */
object FeaturesConf {
  val file: LoadFiles = new LoadFiles("features.properties")
  val userdetName = file.getProp("clean.name.userdet")
  val userdetPath = SparkConf.hadoopMaster + CleanConf.getProp("clean.business." + userdetName + ".newFileDir")
  val userhisName = file.getProp("clean.name.userhis")
  val userhisPath = SparkConf.hadoopMaster + CleanConf.getProp("clean.business." + userhisName + ".newFileDir")
  val monthStatisticPath = SparkConf.hadoopMaster + file.getProp("monthStatistic.path")
  val featurePath = SparkConf.hadoopMaster + file.getProp("feature.path")
  val totalType = file.getProp("totalType")
}