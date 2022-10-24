package com.ainsg.lwfx.config
import com.ainsg.lwfx.util.LoadFiles
import com.ainsg.lwfx.clean.config.CleanConf

object TerminalConf {
  /**
   * 目录都要加 hdfs url
   */
  
  
  val file: LoadFiles = new LoadFiles("terminal.properties")
  
  /** 话单清洗配置标识 */
  val userdet_name = file.getProp("clean.name.userdet")
  /** 话单清洗后目录 */
  val userdetPath = SparkConf.hadoopMaster + CleanConf.getProp("clean.business."+userdet_name+".newFileDir")
  
  /** 认证失败数据清洗标识 */
  val userauthfail = file.getProp("clean.name.userauthfail")
  /** 认证失败数据清洗后目录 */
  val userauthfailPath = SparkConf.hadoopMaster + CleanConf.getProp("clean.business."+userauthfail+".newFileDir")
  
  
  /** 质差终端结果用户存放临时目录 */
  val terminalUserTmpDir = SparkConf.hadoopMaster + file.getProp("terminal.user.tmpdir")
  
  /** 质差终端结果用户最终存放目录 */
  val terminalUserDir = SparkConf.hadoopMaster + file.getProp("terminal.user.dir")
  
  
  /** 用户信息配置-- start */
  
  /** 用户信息localDir */
  val userInfoLocalDir = SparkConf.hadoopMaster + file.getProp("terminal.userinfo.localdir")
  /** 用户文件分割符 */
  val userInfoSplit = file.getProp("terminal.userinfo.split")
  /** 用户信息字段信息 */
  val userInfoField = file.getProp("terminal.userinfo.field")
  
  val igatewayMonthStatisticPath =SparkConf.hadoopMaster +  file.getProp("igatewayMonthStatistic.path")
  val igatewayFeaturePath = SparkConf.hadoopMaster + file.getProp("igatewayFeature.path")
  val igatewayTotalType = file.getProp("igatewayTotalType") 
  val dhcpName = file.getProp("clean.name.dhcp")
  val dhcpPath = SparkConf.hadoopMaster + CleanConf.getProp("clean.business." + dhcpName + ".newFileDir")
  val igatewayName = file.getProp("clean.name.igateway")
  val igatewayPath = SparkConf.hadoopMaster + CleanConf.getProp("clean.business." + igatewayName + ".newFileDir")

  /** IEEE目录 */
  val ieeePath = SparkConf.hadoopMaster + file.getProp("ieeefile.path")
  
  val terminalPath = SparkConf.hadoopMaster + file.getProp("terminal.path")
}