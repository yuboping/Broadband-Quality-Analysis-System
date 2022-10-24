package com.ainsg.lwfx.clean.service

//清洗数据配置类
class CleanConfig(nameValue: String,localDirValue: String,newFileDirValue: String,isCityValue: String,fieldValue: String,
    tableValue: String,selectFieldValue: String,filterConditionValue: String,saveModeValue: String,
    fileSuffixValue: String,monthsValue: String, splitValue: String, cityCodeValue: String, regionNumValue: String,
    formatValue: String){
  // 业务名称
  var name:String = nameValue
  // 源文件目录
  var localDir:String = localDirValue
  var newFileDir:String = newFileDirValue
  
  /**
   * 是否根据city分目录
   * 0: 按/201801 目录存放数据  1：按 /201801/city/ 格式存放数据
   */
  var isCity:Int = if(isCityValue==null || isCityValue.equals("")) 0 else isCityValue.toInt
  // 字段名称，逗号分隔
  var field:String = fieldValue
  // 表名称
  var table:String = if(tableValue==null || tableValue.equals("")) "table" else tableValue
  // 要查找的字段名称
  var selectField:String = if(selectFieldValue==null || selectFieldValue.equals("")) "*" else selectFieldValue
  // 过滤条件
  var filterCondition:String = filterConditionValue
  // 文件名后缀名称
  var fileSuffix:String = if(fileSuffixValue==null) "" else fileSuffixValue
  
  // 月数
  var months:Int = if(monthsValue==null || monthsValue.equals("")) 1 else monthsValue.toInt
  // 文件保存类型
  var saveMode:String = if(saveModeValue==null || saveModeValue.equals("")) "Append" else saveModeValue
  /** 数据分割符 */
  var split:String = if(splitValue==null || splitValue.equals("")) "," else splitValue
  
  // 地市编号字段名称
  var cityCode:String = cityCodeValue
  
  // 地市转换名称
  var regionNum:String = if(regionNumValue == null) "" else regionNumValue
  
  //文件类型
  var format: String = if(formatValue==null || formatValue.equals("")) "text" else formatValue
}