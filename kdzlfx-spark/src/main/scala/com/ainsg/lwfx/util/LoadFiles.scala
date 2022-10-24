package com.ainsg.lwfx.util
import java.util.Properties
/**
 * 加载配置文件
 */
class LoadFiles(filname: String) {
  val sysprop = new Properties()
  sysprop.load(this.getClass.getClassLoader().getResourceAsStream("system.properties"))
  val province = sysprop.getProperty("system.province").trim()
  val prop = new Properties()
  prop.load(this.getClass.getClassLoader().getResourceAsStream(province + "/" + filname))

  def getProp(field: String): String = {
    var value = prop.getProperty(field)
    if (value != null) {
      value.trim()
    } else {
      ""
    }
  }
  
  /**
   * 直接返回对应的值，不做处理
   */
  def getPropNull(field: String): String = {
    var value = prop.getProperty(field)
    if (value != null) {
      value.trim()
    } else {
      value
    }
  }
  
}
