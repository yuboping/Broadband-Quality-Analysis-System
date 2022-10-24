package com.ainsg.lwfx.service

import com.ainsg.lwfx.common.Spark.spark
import com.ainsg.lwfx.common.Spark.spark.implicits._
import com.ainsg.lwfx.transformer.DataColumns
import com.ainsg.lwfx.util.TimeTool
import com.ainsg.lwfx.util.HadoopTool
import com.ainsg.lwfx.util.FileUtil
import org.apache.spark.sql.{ SaveMode, DataFrame, Row, SparkSession }
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ainsg.lwfx.config.TerminalConf
/**
 * 返回DataFrame
 */
object UserInfoService {
  val LOG: Logger = LoggerFactory.getLogger(UserInfoService.getClass)
  def getUserInfo(): DataFrame = {
    LOG.info("加载用户数据信息")
    var userData: DataFrame = null
    val localDir = TerminalConf.userInfoLocalDir
    
    if(!HadoopTool.isExitDir(localDir)) {
      LOG.error("目录："+localDir+" 不存在")
      return userData
    }
    val filed = TerminalConf.userInfoField
    val split = TerminalConf.userInfoSplit.charAt(0)
    val filedNum: Int = filed.count(_ == ',')
    LOG.info("userInfo data load")
    val rddata = spark.read.format("text").load(localDir).as[String]
    val data = DataColumns.RDDToDF(spark, rddata.rdd, filed, "0", TerminalConf.userInfoSplit)
    userData = data.select("USERNAME", "AREANO").where("USERNAME != 'username'").distinct()
//    userData.show()
    userData
  }
  
}