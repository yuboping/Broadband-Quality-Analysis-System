package com.ainsg.lwfx.util

/** Created by ShiGZ on 2016/10/13. */
object StringTool {

  /**
    *
    * @param text text
    * @return 是否有值
    */
  def hasValue(text: String): Boolean = {
    notNull(text) && !text.isEmpty
  }

  /** 无论有没有值，都使用分隔符将传入的选项拼接起来
    *
    * @param separator  separator
    * @param firstValue firstValue
    * @param  values    values
    * @return
    */
  def getJoinRequiredValues(separator: Any, firstValue: Any, values: Any*): String = {
    val result = new StringBuilder
    result.append(firstValue)
    for (value <- values) {
      result.append(separator).append(value)
    }
    result.toString()
  }

  /** 将多个值组合起来拼接成一个字符串
    *
    * @param values 可变值集合
    * @return 拼接好的字符串
    */
  def joinValues(values: Any*): String = {
    val result = new StringBuilder
    for (value <- values) {
      if (notNull(value)) {
        result.append(value)
      }
    }
    result.toString
  }

  /** 将多个值组合起来拼接成一个字符串
    *
    * @param values 可变值集合
    * @return 拼接好的字符串
    */
  def joinListValues(values: List[Any]): String = {
    val result = new StringBuilder
    for (value <- values) {
      if (notNull(value)) {
        result.append(value)
      }
    }
    result.toString
  }

  /** 判断某个值是否为空
    *
    * @param text text
    * @return 是否为空
    */
  def notNull(text: Any): Boolean = {
    text != null
  }

}