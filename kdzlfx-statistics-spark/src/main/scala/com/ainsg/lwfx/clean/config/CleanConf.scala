package com.ainsg.lwfx.clean.config
import com.ainsg.lwfx.util.LoadFiles
/**
 * 数据清洗相关配置
 */
object CleanConf {
  val file: LoadFiles = new LoadFiles("clean.properties")
  val hadoopMaster = file.getProp("hadoop.master")
  
  def getProp(field: String): String = {
    file.getProp(field)
  }
}