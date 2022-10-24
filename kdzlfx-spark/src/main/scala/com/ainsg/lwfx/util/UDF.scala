package com.ainsg.lwfx.util

import com.ainsg.lwfx.util.TimeTool.StringToDate
import com.ainsg.lwfx.util.TimeTool.dateToString
import com.ainsg.lwfx.util.TimeTool.getMonthDate
import com.ainsg.lwfx.util.TimeTool.monthFirstDay
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

object UDF {
  val LOG: Logger = LoggerFactory.getLogger(TimeTool.getClass)
  /**
   * 自定义decode函数
   * @param tiaojian
   * @param v1
   * @param v2
   * @param v3
   * @return
   */
  def myDecode(tiaojian: Int, v1: Int, v2: Int, v3: Int) = {
    if (tiaojian == v1) {
      v2
    } else {
      v3
    }
  }
  def myDateFormat(dateStr: String): String = {
    val sDateStr = monthFirstDay(dateStr)
    sDateStr
  }
  def myMonthFormat(dateStr: String): String = {
    val sDateStr = getMonthDate(dateStr)
    sDateStr
  }
  def mycrmdecode(v1: String) = {
    v1.replaceAll("/", "-").substring(0, 10)
  }

  /**
   * 日期字符转换格式
   * oldFormat 转换为 newFormat
   */
  def strToDateToStr(dateStr: String, oldFormat: String, newFormat: String) = {
    if (dateStr == null || dateStr.equals("")) {
      ""
    } else {
      val date = StringToDate(dateStr, oldFormat)
      val str = dateToString(date, newFormat)
      str
    }
  }

  /**
   * 字符为空时，赋值 nullVal
   * 不为空时，赋值 isNullVal
   */
  def strNullSetVal(dataStr: String, nullVal: String, isNotNullVal: String) = {
    if (dataStr == null || dataStr.equals("")) {
      nullVal
    } else {
      isNotNullVal
    }
  }

  /**
   * 16进制转换10进制函数
   */
  def sixteenTransTen(a16: String) = {
    try {
      ToolsUtil.sixteenTransTen(a16)
    } catch {
      case ex: Exception =>
        LOG.error("16进制转换10进制函数:a16-->", a16, " 函数异常！异常信息为： " + ex.getStackTrace, ex)
        0
    }
  }

  /**
   * 10进制转换16进制函数(不到6位的前面用0补齐)
   */
  def tenTransSixteen(a10: Int) = {
    try {
      var a16 = ToolsUtil.tenTransSixteen(a10)
      var a16Length = a16.length()
      if (a16Length < 6) {
        for (i <- 1 to 6 - a16Length) {
          a16 = "0" + a16
        }
      }
      a16
    } catch {
      case ex: Exception =>
        LOG.error("10进制转换16进制函数:a10-->", a10.toString(), " 函数异常！异常信息为： " + ex.getStackTrace, ex)
        "0"
    }
  }

  /**
   * 添加冒号：ecbc9b -->  ec:bc:9b
   */
  def addColon(a16: String) = {
    try {
      val one = a16.substring(0, 2)
      val two = a16.substring(2, 4)
      val three = a16.substring(4, 6)
      var a16Colon = one + ":" + two + ":" + three
      a16Colon
    } catch {
      case ex: Exception =>
        LOG.error("添加冒号函数:a10-->", a16, " 函数异常！异常信息为： " + ex.getStackTrace, ex)
        "0"
    }
  }
  
  /**
   * 质差终端计算健康值
   */
  def calcHealthValue(authfailFlag: Integer, shorttimeFlag: Integer, oftendownFlag: Integer) = {
    var healthValue: Integer = 0
    if(authfailFlag + shorttimeFlag + oftendownFlag == 3) {
      healthValue = 40
    } else if (authfailFlag + shorttimeFlag + oftendownFlag == 2) {
      healthValue = 60
    } else if (authfailFlag + shorttimeFlag + oftendownFlag == 1) {
      healthValue = 80
    } else if (authfailFlag + shorttimeFlag + oftendownFlag == 0) {
      healthValue = 100
    }
    healthValue
  }
  
  /**
   * 质差终端判断是否是质差
   */
  def calcBQ(authfailFlag: Integer, shorttimeFlag: Integer, oftendownFlag: Integer) = {
    var badQuality: Integer = 0
    if(authfailFlag + shorttimeFlag + oftendownFlag == 0) {
      badQuality = 0
    } else {
      badQuality = 1
    }
    badQuality
  }

  /**
   * 投诉特征计算健康值
   */
  def complaintHealth(complaintTotal: Integer) = {
    var healthValue: Integer = 0
    if(complaintTotal == 0) {
      healthValue = 100
    } else if (complaintTotal > 0 && complaintTotal <= 5) {
      healthValue = 80
    } else if (complaintTotal > 5) {
      healthValue = 60
    }
    healthValue
  }

//  def main(args: Array[String]): Unit = {
//    var test = calcBQ(1,1,1)
//    print(test)
//  }

}
