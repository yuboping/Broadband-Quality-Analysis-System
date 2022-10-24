package com.ainsg.lwfx.terminal.config
import com.ainsg.lwfx.util.LoadFiles

object TerminalConf {
  val file: LoadFiles = new LoadFiles("terminal.properties")
  
  /** 话单清洗配置标识 */
  val userdet_name = file.getProp("clean.name.userdet")
  
  /** 认证失败数据清洗标识 */
  val userauthfail = file.getProp("clean.name.userauthfail")
  
  /** 质差终端结果用户存放临时目录 */
  val terminalUserTmpDir = file.getProp("terminal.user.tmpdir")
  
  /** 质差终端结果用户最终存放目录 */
  val terminalUserDir = file.getProp("terminal.user.dir")
  
  
  
  /** 用户信息配置-- start */
  
  /** 用户信息localDir */
  val userInfoLocalDir = file.getProp("terminal.userinfo.localdir")
  /** 用户文件分割符 */
  val userInfoSplit = file.getProp("terminal.userinfo.split")
  /** 用户信息字段信息 */
  val userInfoField = file.getProp("terminal.userinfo.field")
  
  val userdetPath = file.getProp("userdet.path")
  val igatewayMonthStatisticPath = file.getProp("igatewayMonthStatistic.path")
  val igatewayFeaturePath = file.getProp("igatewayFeature.path")
  val igatewayTotalType = file.getProp("igatewayTotalType")
  val dhcpPath = file.getProp("dhcp.path")
  val igatewayPath = file.getProp("igateway.path")
  
  /** IEEE目录 */
  val ieeePath = file.getProp("ieeefile.path")
}