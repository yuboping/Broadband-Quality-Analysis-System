package com.ainsg.lwfx.util

import scala.collection.mutable.ArrayBuffer

/**
  * Created by ShiGZ on 2016/10/20.
  */
object ArrayTool {

  /** 将字符串根据分隔符分割成字符串数组
    *
    * @param msg       msg
    * @param separator separator
    * @return
    */
  def getTextArrWithSeparator(msg: String, separator: String): Array[String] = {
    val resultArr = new ArrayBuffer[String]()
    val separatorLength = separator.length
    var preIndex = 0
    var currentIndex = msg.indexOf(separator, preIndex)
    while (-1 != currentIndex) {
      resultArr += msg.substring(preIndex, currentIndex)
      preIndex = currentIndex + separatorLength
      currentIndex = msg.indexOf(separator, preIndex)
    }
    resultArr += msg.substring(preIndex)
    resultArr.toArray
  }

  /** 将字符串根据分隔符分割成字符串数组
    *
    * @param msg       msg
    * @param separator separator
    * @return
    */
  def getIntArrWithSeparator(msg: String, separator: String): Array[Int] = {
    val resultArr = new ArrayBuffer[Int]()
    val separatorLength = separator.length
    try {
      var preIndex = 0
      var currentIndex = msg.indexOf(separator, preIndex)
      while (-1 != currentIndex) {
        resultArr += msg.substring(preIndex, currentIndex).toInt
        preIndex = currentIndex + separatorLength
        currentIndex = msg.indexOf(separator, preIndex)
      }
      resultArr += msg.substring(preIndex).toInt
      resultArr.toArray
    } catch {
      case ex: Exception => throw new RuntimeException(StringTool.joinValues("使用分隔符 ", separator, " 将字符串 ", msg, " 分割成Int数组时出现了异常！异常信息为： ", ex.getStackTrace), ex)
    }
  }

  def getTextOptionByIndex(fields: Array[String], index: Int): Option[String] = {
    if (0 <= index && index < fields.length) {
      val value = fields(index)
      if (StringTool.hasValue(value)) {
        Some(value)
      } else {
        None
      }
    } else {
      None
    }
  }

  def getNotNullTextByIndex(fields: Array[String], index: Int): String = {
    if (0 <= index && index < fields.length) {
      val value = fields(index)
      if (StringTool.hasValue(value)) {
        value
      } else {
        ""
      }
    } else {
      ""
    }
  }

}
