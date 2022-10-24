package com.ainsg.lwfx.crm.config
import com.ainsg.lwfx.util.LoadFiles
/**
 * crm数据合成相关配置
 */
object CrmConf {
  val file: LoadFiles = new LoadFiles("crm.properties")

  val hadoopMaster = file.getProp("hadoop.master")

  /** 数据日期格式 */
  val dateFormat = file.getProp("crm.dateFormat")
  /** 字符编码 */
  val code = file.getProp("crm.code")
  /** 地市存储开关 1：是， 0 否  */
  val isCitySave: Int = if (file.getProp("crm.isCitySave") == null || file.getProp("crm.isCitySave").equals("")) 0 else file.getProp("crm.isCitySave").toInt
  /** 数据源路径 */
  val localdir = hadoopMaster + file.getProp("crm.localdir")
  /** 按数据来源类型 */
  val dataSourceType = file.getProp("crm.dataSourceType")
  /** 按数据文件类型ALL */
  val dataTypeAll = file.getProp("crm.dataTypeAll")
  /** 按数据文件类型ALL */
  val dataTypeFEE = file.getProp("crm.dataTypeFEE")
  /** 按数据文件类型ALL */
  val dataTypeREMOVE = file.getProp("crm.dataTypeREMOVE")
  /** 数据分类后路径 */
  val newDir = hadoopMaster + file.getProp("crm.newDir")

  val allDir = file.getProp("crm.ALLDir")
  val feeDir = file.getProp("crm.FEEDir")
  val removeDir = file.getProp("crm.REMOVEDir")
  /** 数据全量文件字段 */
  val allField = file.getProp("crm.ALL.field")
  /** 数据全量宽带资料字段 */
  val feeField = file.getProp("crm.FEE.field")
  /** 数据全量宽带用户费用字段 */
  val removeField = file.getProp("crm.REMOVE.field")

  /** 数据分割符 */
  val splitstr = file.getProp("crm.splitstr")

  /** 地市编码数据对应字段 */
  val cityConversionField = file.getProp("crm.cityConversion.field")

  /** 最终合成crm数据存储路径 */
  val dataDir = file.getProp("crm.dataDir")

  /** 省市编码 */
  val cityCodes = file.getProp("crm.city.codes")

  /** 数据省市编码 */
  val cityCodevalues = file.getProp("crm.city.codevalues")
}