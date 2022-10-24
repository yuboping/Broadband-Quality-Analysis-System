package com.ainsg.lwfx.util

import scala.collection.mutable.ArrayBuffer

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 工具类
 */
object ToolsUtil {
  val LOG: Logger = LoggerFactory.getLogger(ToolsUtil.getClass)

  /**
   * 判断字符串是否为空
   * 为空返回true
   */
  def isNullStr(str: String): Boolean = {
    var rt = false
    if ("".equals(str) || null == str) {
      rt = true
    }
    rt
  }

  /**
   * 返回字符串分割之后的数组
   */
  def getSplitArrayBuffer(info: String, splitStr: String): ArrayBuffer[String] = {
    var result = new ArrayBuffer[String]()
    var splitArr = info.split(splitStr)
    splitArr.foreach(x => { result.append(x) })
    result
  }

  /**
   * 计算幂次方
   */
  def expr(x: Int, n: Int): Int = {
    var result = 1
    for (i <- 0 until n) {
      result = result * x
    }
    result
  }

  /**
   * 16进制转换10进制
   */
  def sixteenTransTen(a16: String): Int = {
    var a10 = 0
    var j = 0
    for (i <- (0 until a16.length).reverse) {
      val tai = a16(i) match {
        case '0' => 0
        case '1' => 1
        case '2' => 2
        case '3' => 3
        case '4' => 4
        case '5' => 5
        case '6' => 6
        case '7' => 7
        case '8' => 8
        case '9' => 9
        case 'a' => 10
        case 'b' => 11
        case 'c' => 12
        case 'd' => 13
        case 'e' => 14
        case 'f' => 15
        case _   => 0
      }
      a10 = a10 + (tai * expr(16, j))
      j = j + 1
    }
    a10
  }

  /**
   * 10进制转换16进制
   */
  def tenTransSixteen(a10: Int): String = {
    val floor = Math.floor _ //向下取整
    var a16_0 = "0"
    var a16 = ""
    var a10d = a10
    var b = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    while (a10d != 0) {
      a16 = a16 + (b(a10d % 16))
      a10d = Math.floor(a10d / 16).toInt
    }
    a16 = a16.reverse
    if (isNullStr(a16)) {
      a16_0
    } else {
      a16
    }
  }

  //  def main(args: Array[String]): Unit = {
  //    var a11 = tenTransSixteen(15514779)
  //    print(a11)
  //  }

}