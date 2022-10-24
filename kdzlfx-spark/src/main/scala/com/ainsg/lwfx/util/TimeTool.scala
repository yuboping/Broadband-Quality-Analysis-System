package com.ainsg.lwfx.util

import java.text.SimpleDateFormat
import java.util.{ Calendar, Date }
import scala.collection.mutable.ArrayBuffer
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/** Created by ShiGZ on 2016/8/25. */
object TimeTool {
  val LOG: Logger = LoggerFactory.getLogger(TimeTool.getClass)
  val DEFAULT_WEED_FORMAT = new SimpleDateFormat("yyyyww")

  val DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
  val now: Date = new Date()
  /**
   * 根据格式获得时间格式化器
   *
   * @param format format
   * @return
   */
  def getTimeFormat(format: String): SimpleDateFormat = {
    new SimpleDateFormat(format)
  }
  def StringToDate(dateString: String, format: String): Date = {
    val sdf = new SimpleDateFormat(format)
    sdf.parse(dateString)
  }

  /**
   * 时间格式转换：
   * 年周：yyyyww
   * 年月：yyyyMM
   * @param time
   * @param format
   * @return
   */
  def dateToString(time: Date, format: String): String = {
    val sdf = new SimpleDateFormat(format)
    sdf.format(time)
  }
  /**
   * 获取昨天日期，格式yyyyMMdd
   */
  def today(): String = {
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMMdd")
    val hehe = dateFormat.format(now)
    return hehe
  }
  def yesterday(): String = {
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMMdd")
    val hehe = dateFormat.format(getBeforeDate(now, -1))
    return hehe
  }
  def month(): String = {
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMM")
    val hehe = dateFormat.format(now)
    return hehe
  }
  def beforeMonth(num: Int): String = {
    val calendar: Calendar = Calendar.getInstance();
    //将calendar装换为Date类型
    val date: Date = calendar.getTime();
    //获取当前时间的前6个月
    calendar.add(Calendar.MONTH, -num);
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMM")
    val hehe = dateFormat.format(calendar.getTime)
    return hehe
  }
  //获取本月第一天
  def getNowMonthStart(): String = {
    val cal: Calendar = Calendar.getInstance();
    cal.set(Calendar.DATE, 1)
    cal.getTime() //本月第一天
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMMdd")
    val hehe = dateFormat.format(cal.getTime)
    return hehe
  }
  //获取本月第一天
  def getNowMonthStartDate(): Date = {
    var period: String = ""
    var cal: Calendar = Calendar.getInstance();
    cal.set(Calendar.DATE, 1)
    cal.getTime() //本月第一天
  }
  /**
   * 取得给定日期的前、后多少天
   * @param date 给定日期
   * @param days 负数为前推，正数为后推
   * @return
   */
  def getBeforeDate(date: Date, days: Int): Date = {
    val calendar: Calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + days);
    return calendar.getTime();
  }

  /**
   * 获得默认格式的当前时间
   *
   * @return
   */
  def currentTime(): String = {
    DEFAULT_DATE_FORMAT.format(new Date())
  }

  /**
   * 使用输入的时间格式化器，将输入时间转换成Long型毫秒数
   *
   * @param textTime   textTime
   * @param timeFormat timeFormat
   * @return
   */
  def getMilliSeconds(textTime: String, timeFormat: SimpleDateFormat): Long = {
    timeFormat.parse(textTime).getTime
  }

  /**
   * 使用输入的时间格式化器，将输入时间转换成Long型毫秒数,由于有超过格式化器识别长度的现象，所以需要对时间字段进行截取
   *
   * @param textTime         textTime
   * @param timeFormat       timeFormat
   * @param timeFormatLength timeFormatLength
   * @return
   */
  def getMilliSeconds(textTime: String, timeFormat: SimpleDateFormat, timeFormatLength: Int): Long = {
    timeFormat.parse(textTime.substring(0, timeFormatLength)).getTime
  }

  /**
   * 使用输入的时间格式化器将输入时间格式化成Long型毫秒数，如果转换失败，返回None
   *
   * @param textTimeOption textTimeOption
   * @param timeFormat     timeFormat
   * @return
   */
  def getMilliSecondsOption(textTimeOption: Option[String], timeFormat: SimpleDateFormat): Option[Long] = {
    if (textTimeOption.isDefined && textTimeOption.get.nonEmpty) {
      val time = textTimeOption.get
      try {
        Some(getMilliSeconds(time, timeFormat))
      } catch {
        case ex: Exception =>
          LOG.error("将时间 " + time + " 格式化为毫秒数时出现了异常！异常信息为:" + ex.getStackTrace, ex)
          None
      }
    } else {
      None
    }
  }

  /**
   * 使用输入的时间格式化器将输入时间格式化成Long型毫秒数，如果转换失败，返回None,由于有超过格式化器识别长度的现象，所以需要对时间字段进行截取
   *
   * @param textTimeOption   textTimeOption
   * @param timeFormat       timeFormat
   * @param timeFormatLength timeFormatLength
   * @return
   */
  def getMilliSecondsOption(textTimeOption: Option[String], timeFormat: SimpleDateFormat, timeFormatLength: Int): Option[Long] = {
    if (textTimeOption.isDefined && textTimeOption.get.nonEmpty) {
      val time = textTimeOption.get
      try {
        Some(getMilliSeconds(time, timeFormat, timeFormatLength))
      } catch {
        case ex: Exception =>
          LOG.error("将时间 " + time + " 格式化为毫秒数时出现了异常！异常信息为:" + ex.getStackTrace, ex)
          None
      }
    } else {
      None
    }
  }

  /**
   * 获得当前日期是今年第几天
   *
   * @return
   */
  def currentDayOfYear(): Int = {
    Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
  }

  /**
   * 获得当前时间是今天第几个小时
   *
   * @return
   */
  def currentHourOfDay(): Int = {
    Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
  }
  /**
   * 获得当前月份
   * @return
   */
  def nowMonth(): String = {
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM")
    val hehe = dateFormat.format(now)
    return hehe
  }
  /**
   * 获得月份的第一天yyyy-MM-dd
   * @return
   */
  def monthFirstDay(time: String): String = {
    val date = StringToDate(time, "yyyy/MM")
    val sdf = new SimpleDateFormat("yyyy-MM")
    sdf.format(date) + "-01"
  }
  def monthFirstDay(time: String, formatStr: String): String = {
    val date = StringToDate(time, formatStr)
    val sdf = new SimpleDateFormat("yyyy-MM")
    val sdDate = sdf.format(date)
    val ffDate = sdDate + "-01"
    ffDate
  }
  def getMonthDate(time: String): String = {
    val date = StringToDate(time, "yyyy-MM-dd")
    val sdf = new SimpleDateFormat("yyyyMM")
    sdf.format(date)
  }

  def getMonths(months: Int): Array[String] = {
    val monthArray: Array[String] = new Array[String](months)
    val calendar = Calendar.getInstance();
    val today = calendar.get(Calendar.DAY_OF_MONTH)
    if (today == 1) {
      //不取本月，从上月开始计算
      for (a <- 1 to months) {
        monthArray(a - 1) = beforeMonth(a)
      }
    } else {
      // 从本月开始计算
      for (a <- 0 until months) {
        monthArray(a) = beforeMonth(a)
      }
    }
    monthArray
  }

  /**
   * 计算nowM减去num月的月份
   */
  def beforeMonth(nowM: String, num: Int): String = {
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMM")
    val date: Date = dateFormat.parse(nowM)
    val cal: Calendar = Calendar.getInstance()
    cal.setTime(date);
    cal.add(Calendar.MONTH, -num)
    val preM = dateFormat.format(cal.getTime())
    preM
  }
  /**
   * 计算nowM前N个月的日期，返回字符串数组["201801","201712","201711"]
   */
  def PreNmonth(nowM: String, num: Int): ArrayBuffer[String] = {
    val result = new ArrayBuffer[String]()
    var i = 0
    for (i <- 1 to num) {
      result.append(TimeTool.beforeMonth(nowM, i))
    }
    result
  }

  /**
   * 获取 month yyyyMM 月份 缺失周数
   *  { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" }
   *       1        2         3          4          5          6        7
   */
  def getMonthmMissWeek(month: String): ArrayBuffer[String] = {
    val result = new ArrayBuffer[String]()
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMM")
    val date: Date = dateFormat.parse(month)
    val cal: Calendar = Calendar.getInstance()
    cal.setTime(date);
    //判断月初是否为周一
    cal.set(Calendar.DAY_OF_MONTH, 1);
    var w = cal.get(Calendar.DAY_OF_WEEK)
    var datew = ""
    if (w != 2) {
      datew = dateToString(cal.getTime(), "yyyyww")
      result.append(datew)
    }
    //判断月末是否为周日
    cal.add(Calendar.MONTH, 1);
    cal.set(Calendar.DAY_OF_MONTH, 0);
    w = cal.get(Calendar.DAY_OF_WEEK)
    if (w != 1) {
      datew = dateToString(cal.getTime(), "yyyyww")
      result.append(datew)
    }
    result
  }

  /**
   * 计算本月有多少小时
   */
  def monthHour(month: String): Int = {
    val curMonthFirstDay = monthFirstDay(month, "yyyyMM");
    val nextMonthFirstDay = monthFirstDay(beforeMonth(month, -1), "yyyyMM");
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val beginDay = dateFormat.parse(curMonthFirstDay).getTime
    val endDay = dateFormat.parse(nextMonthFirstDay).getTime
    val monthHour = ((endDay - beginDay) / (1000 * 60 * 60)).toInt
    monthHour
  }

//  def main(args: Array[String]): Unit = {
//    val missWeek = getMonthmMissWeek("201801")
//    var whereConditon = ""
//    missWeek.foreach(week => {
//      whereConditon = week + ","
//    })
//    println(whereConditon)
//    whereConditon = whereConditon.substring(0, whereConditon.length() - 1)
//    whereConditon = " where CITY_CODE NOT IN (" + whereConditon + ")"
//    println(whereConditon)
//  }

}
