package com.ainsg.lwfx.crm.service
import com.ainsg.lwfx.common.Spark.spark
import com.ainsg.lwfx.common.Spark.spark.implicits._
import com.ainsg.lwfx.util.{ HadoopTool, TimeTool }
import org.apache.spark.sql.{ DataFrame, SaveMode }
import com.ainsg.lwfx.features.config.FeaturesConf
import org.slf4j.{ Logger, LoggerFactory }
import scala.collection.mutable.ArrayBuffer
import com.ainsg.lwfx.transformer.DataColumns
import com.ainsg.lwfx.crm.config.CrmConf
import scala.util.control._
import org.apache.hadoop.mapred.TextInputFormat
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import java.io.FileNotFoundException
import org.apache.spark.storage.StorageLevel

/**
 * crm数据合成
 */
object CrmService {
  val LOG: Logger = LoggerFactory.getLogger(CrmService.getClass)

  def classifyCrmData(dataSourceTypeArr: Array[String]): Unit = {
    /** 获取localdir目录下所有文件名 */
    val fileNameArray: ArrayBuffer[String] = HadoopTool.getFileNameABFromPath(CrmConf.localdir)
    for (filename <- fileNameArray) {
      classifyCrmDataDetail(filename, dataSourceTypeArr)
    }
  }

  /**
   * crm数据分类：
   * 按数据来源dataSourceType 数据类型dataType 分类
   * /crm/1702/all/201803 /crm/1702/fee/201803 /crm/1702/remove/201803
   */
  def classifyCrmDataDetail(filename: String, dataSourceTypeArr: Array[String]): Unit = {
    val sourceTypedir = getDirByDataSourceType(filename, dataSourceTypeArr)
    val dataTypeDir = getDirByDataType(filename)
    //获取文件类型
    if (!sourceTypedir.equals("") && !dataTypeDir.equals("")) {
      //组合新路径
      val target = CrmConf.newDir + "/" + sourceTypedir + "/" + dataTypeDir + "/" + filename.substring(0, 6) + "/"
      //hadoop hdfs 复制操作
      val filepath = CrmConf.localdir + "/" + filename
      LOG.info("copy " + filepath + " to " + target)
      HadoopTool.moveFile(filepath, target)
    }
  }

  /**
   * 根据文件来源获取路径
   */
  def getDirByDataSourceType(filename: String, dataSourceTypeArr: Array[String]): String = {
    var sourceTypedir: String = ""
    val loop = new Breaks;
    //获取文件名来源类型
    loop.breakable {
      for (sourceType <- dataSourceTypeArr) {
        if (filename.contains(sourceType)) sourceTypedir = sourceType
        if (!sourceTypedir.equals("")) loop.break
      }
    }
    sourceTypedir
  }

  /**
   * 获取文件对应响应类型的路径
   */
  def getDirByDataType(filename: String): String = {
    var dataTypeDir = ""
    if (filename.contains(CrmConf.dataTypeAll)) {
      dataTypeDir = CrmConf.allDir
    } else if (filename.contains(CrmConf.dataTypeFEE)) {
      dataTypeDir = CrmConf.feeDir
    } else if (filename.contains(CrmConf.dataTypeREMOVE)) {
      dataTypeDir = CrmConf.removeDir
    }
    dataTypeDir
  }

  /**
   * 加载数据，并返回DataFrame
   * @param source 数据目录
   * @param fieldstr 数据对应字段
   * @param splitstr 数据分割符
   */
  def loadData(source: String, fieldstr: String, splitstr: String): DataFrame = {
    var data: DataFrame = null
    if (HadoopTool.isExitDir(source)) {
      val filedNum: Int = 32
      val rddData = spark.sparkContext.hadoopFile(source, classOf[TextInputFormat], classOf[LongWritable], classOf[Text])
        .map(p => new String(p._2.getBytes, 0, p._2.getLength, CrmConf.code))
      data = DataColumns.RDDToDF(spark, rddData, fieldstr, splitstr)
    } else {
      LOG.info("---------" + source + "---------------isNotExit,create null data ")
      data = createNullDataFram(fieldstr, splitstr)
    }
    LOG.info("---------" + source + "---------------show")
    data.show()
    data
  }

  def createNullDataFram(field: String, splitstr: String): DataFrame = {
    val fieldArr = field.split(",")
    var str = ""
    for (i <- 0 to (fieldArr.length - 2)) {
      str = str + splitstr
    }
    val list: List[String] = List(str)
    val data = spark.createDataset(list)
    val allCrmData = DataColumns.RDDToDF(spark, data.rdd, field, splitstr)
    allCrmData
  }
  /**
   * 加载地市编码转换数据
   */
  def initCityData() = {
    val cityCode = CrmConf.cityCodes.split(",")
    val cityCodeValues = CrmConf.cityCodevalues.split(",")
    var list: List[String] = List()
    for (i <- 0 to (cityCode.length - 1)) {
      val data = cityCode(i) + "," + cityCodeValues(i)
      list = list :+ data
    }
    val data = spark.createDataset(list)
    val cityData = DataColumns.RDDToDF(spark, data.rdd, CrmConf.cityConversionField)
    cityData.createTempView("CIYT_CONVER")
  }

  /**
   * 数据合成
   * 1、数据分类存储
   * 2、数据逻辑处理
   */
  def dealCrm(startMonth: String, dataSourceTypeArr: Array[String]): Unit = {
    //加载 city_code.txt 文件,后续地市转换
    LOG.info("加载地市信息，load city info")
    initCityData()
    val lastMonth = TimeTool.beforeMonth(startMonth, 1)
    var resultData: DataFrame = null
    for (i <- 0 to (dataSourceTypeArr.length - 1)) {
      val data = handleDataByMonth(startMonth, lastMonth, dataSourceTypeArr(i))
      if (i == 0) {
        resultData = data
      } else {
        resultData.union(data)
      }
    }
    // 去除重复数据 并保存数据
    val resultTable = "CRM_RESULT_" + startMonth
    resultData.createOrReplaceTempView(resultTable)
    LOG.info("------------" + startMonth + ": 最终合成数据------------------")
    resultData.show()
    save(resultTable, startMonth)
  }

  /**
   * 去除重复数据 并保存数据
   */
  def save(resultTable: String, month: String) = {
    /**
     * * USER_ID,USER_NAME,EPARCHY_NAME,CUST_NAME,INSTALL_ADRESS,CHARGE_TYPE,FEE_VALUE,BEGIN_TIME,END_TIME,ORDER_TYPE,ORDER_TIME,LINK_PHONE,CITY_CODE,UU_START,UU_END,SPEED,HAS_IPTV
     */

    val sql = "SELECT USER_ID,USER_NAME,EPARCHY_NAME,CUST_NAME,INSTALL_ADRESS,CHARGE_TYPE,FEE_VALUE,BEGIN_TIME,END_TIME,ORDER_TYPE,ORDER_TIME,LINK_PHONE,CITY_CODE,UU_START,UU_END,SPEED,HAS_IPTV " +
      " FROM (select row_number() over(partition by USER_NAME order by BEGIN_TIME desc) rn,* from " + resultTable + " )" +
      " where rn = 1";
    val data = spark.sql(sql)
    val saveDir = CrmConf.dataDir + "/" + month
    if (CrmConf.isCitySave == 1) {
      data.createOrReplaceTempView(resultTable)
      saveByCity(resultTable, month, saveDir)
    } else {
      LOG.info("去除重复数据 并保存数据 save data to " + saveDir)
      data.show()
      data.repartition(1).write.option("delimiter", ",").option("header", true).mode(SaveMode.Overwrite).csv(saveDir)
    }
  }

  def saveByCity(resultTable: String, month: String, saveDir: String) {
    val cityCodelist = CrmConf.cityCodes.split(",")
    for (citycode <- cityCodelist) {
      val saveCityDir = saveDir + "/" + citycode
      val sql = "SELECT * FROM " + resultTable + " WHERE CITY_CODE=" + citycode + ""
      val cityData = spark.sql(sql)
      LOG.info(citycode + " show" + saveCityDir)
      cityData.show()
      val city_ct: Long = cityData.count()
      LOG.info("当前月" + month + "下城市编码为 " + citycode + " 查询数量:", city_ct)
      if (city_ct > 0) {
        LOG.info("去除重复数据 并保存数据 save data to " + saveCityDir)
        cityData.repartition(1).write.option("delimiter", ",").option("header", true).mode(SaveMode.Overwrite).csv(saveCityDir)
      }
    }
  }

  /**
   * 处理来源数据
   */
  def handleDataByMonth(startMonth: String, lastMonth: String,
    dataSourceType: String) = {
    //读取all类型文件：按照类型分为包年、（包月和其他）
    val allData = getAllFeeCrmInfo(startMonth, dataSourceType)
    println("------------getAllFeeCrmInfo-------------------")
    allData.show()
    //合并数据
    var data = allData
    //读取remove类型文件 关联上个月crm数据
    val removeData = getRomoveData(startMonth, lastMonth, dataSourceType)
    LOG.info("---------" + startMonth + " " + dataSourceType + " ---removeData-------------------")
    removeData.show()
    if (removeData.count() > 0) data = allData.union(removeData)
    data.createOrReplaceTempView("CRM_" + dataSourceType + "_" + startMonth)
    LOG.info("---------" + startMonth + " " + dataSourceType + " ---all  data  合成-------------------")
    data.show()
    val data2 = cleanCrmData("CRM_" + dataSourceType + "_" + startMonth)
    LOG.info("------------cleanCrmData-------------------")
    LOG.info("---------" + startMonth + " " + dataSourceType + " ---cleanCrmData-------------------")
    data2.show()
    data2
  }

  /**
   * 数据清洗 地市编码转换 日期字段转统一格式  yyyy/MM/dd HH:mm:ss
   *  USER_NAME|CHARGE_TYPE|FEE_VALUE|BEGIN_TIME|END_TIME|ORDER_TYPE|ORDER_TIME|CITY_CODE
   * USER_ID,USER_NAME,EPARCHY_NAME,CUST_NAME,INSTALL_ADRESS,CHARGE_TYPE,FEE_VALUE,BEGIN_TIME,
   * END_TIME,ORDER_TYPE,ORDER_TIME,LINK_PHONE,CITY_CODE,UU_START,UU_END,SPEED,HAS_IPTV
   *
   */
  def cleanCrmData(crmTable: String) = {
    val sql = "SELECT t1.USER_ID,t1.USER_NAME,t1.EPARCHY_NAME,t1.CUST_NAME,t1.INSTALL_ADRESS," +
      "t1.CHARGE_TYPE,t1.FEE_VALUE, " +
      "strToDateToStr(t1.BEGIN_TIME,'" + CrmConf.dateFormat + "','yyyy/MM/dd HH:mm:ss') AS BEGIN_TIME, " +
      "strToDateToStr(t1.END_TIME,'" + CrmConf.dateFormat + "','yyyy/MM/dd HH:mm:ss') AS END_TIME, " +
      "t1.ORDER_TYPE," +
      "strToDateToStr(t1.ORDER_TIME,'" + CrmConf.dateFormat + "','yyyy/MM/dd HH:mm:ss') AS ORDER_TIME, " +
      "t1.LINK_PHONE,t2.CITY_CODE,t1.UU_START,t1.UU_END,t1.SPEED,t1.HAS_IPTV " +
      "FROM " + crmTable + " t1 LEFT JOIN CIYT_CONVER t2 ON t1.CITY_CODE = t2.EPARCHY_CODE " +
      "WHERE t1.USER_NAME !='' OR t1.USER_NAME != 'null'"
    val data = spark.sql(sql)
    data.show()
    data
  }
  /**
   * 1、取allData 包年的数据 yearData
   * 2、取allData 包月及其他的数据 monthData
   * 3、monthData 关联 feeData 数据，获取 fee 值  newMonthData
   * 4、合并 yearData、newMonthData 并返回
   */
  def getAllFeeCrmInfo(startMonth: String, dataSourceType: String) = {
    //读取all类型文件：按照类型分为包年、（包月和其他）
    val allDir = CrmConf.newDir + "/" + dataSourceType + "/" + CrmConf.allDir + "/" + startMonth
    val feeDir = CrmConf.newDir + "/" + dataSourceType + "/" + CrmConf.feeDir + "/" + startMonth
    val allTable = "CRM_ALL_" + startMonth
    val feeTable = "CRM_FEE_" + startMonth
    cleanAllData(allDir, allTable)
    val feeTableG = cleanFeeData(feeDir, feeTable)
    val yearSql = "SELECT * FROM " + allTable + " WHERE CHARGE_TYPE=2"
    val yearData = spark.sql(yearSql)
    LOG.info("---------" + startMonth + " " + dataSourceType + " ---yearData-------------------")
    yearData.show()
    /**
     * USER_ID,USER_NAME,EPARCHY_NAME,CUST_NAME,INSTALL_ADRESS,CHARGE_TYPE,FEE_VALUE,BEGIN_TIME,
     * END_TIME,ORDER_TYPE,ORDER_TIME,LINK_PHONE,CITY_CODE,UU_START,UU_END,SPEED,HAS_IPTV
     */
    val monthSql = "SELECT t1.USER_ID,t1.USER_NAME,t1.EPARCHY_NAME,t1.CUST_NAME,t1.INSTALL_ADRESS," +
      "t1.CHARGE_TYPE,t2.FEE AS FEE_VALUE,t1.BEGIN_TIME," +
      "t1.END_TIME,t1.ORDER_TYPE,t1.ORDER_TIME,t1.LINK_PHONE,t1.CITY_CODE," +
      "t1.UU_START,t1.UU_END,t1.SPEED,t1.HAS_IPTV " +
      "  FROM " + allTable + " t1 LEFT JOIN " +
      " " + feeTableG + " t2 ON t1.USER_ID = t2.USER_ID " +
      " WHERE t1.CHARGE_TYPE != 2"
    val sql = "SELECT * FROM " + allTable + " WHERE CHARGE_TYPE != 2"
    val sqlData = spark.sql(sql)
    sqlData.createOrReplaceTempView(allTable)
    val monthData = spark.sql(monthSql)
    LOG.info("---------" + startMonth + " " + dataSourceType + " ---monthData-------------------")
    monthData.show()
    val data = yearData.union(monthData)
    data
  }

  /**
   * all 类型数据清洗，返回需要的信息
   * 套餐类型 中文转 数字： 包月：1 ；包年： 2 ；其他 3
   * USER_ID,ACCT_NBR,EPARCHY_NAME,CUST_NAME,INSTALL_ADRESS,PRODUCT_NAME,BB_FEE,START_DATE,END_DATE,DISCNT_NAME,TRADE_DATE,LINK_PHONE,EPARCHY_CODE,UU_START,UU_END,SPEED,HAS_IPTV
   * USER_ID,USER_NAME,EPARCHY_NAME,CUST_NAME,INSTALL_ADRESS,CHARGE_TYPE,FEE_VALUE,BEGIN_TIME,END_TIME,ORDER_TYPE,ORDER_TIME,LINK_PHONE,CITY_CODE,UU_START,UU_END,SPEED,HAS_IPTV
   */
  def cleanAllData(allDir: String, allTable: String) = {
    val data = loadData(allDir, CrmConf.allField, CrmConf.splitstr)
    data.createOrReplaceTempView(allTable)
    val sql = "SELECT USER_ID,ACCT_NBR AS USER_NAME,EPARCHY_NAME,CUST_NAME,INSTALL_ADRESS," +
      "CASE WHEN PRODUCT_NAME=='包月' THEN 1 WHEN PRODUCT_NAME=='包年' THEN 2 ELSE 3 END CHARGE_TYPE," +
      "BB_FEE AS FEE_VALUE,START_DATE AS BEGIN_TIME,END_DATE AS END_TIME,DISCNT_NAME AS ORDER_TYPE," +
      "TRADE_DATE AS ORDER_TIME,LINK_PHONE, EPARCHY_CODE AS CITY_CODE,UU_START,UU_END,SPEED, " +
      "CASE WHEN HAS_IPTV=='包含' THEN 1 ELSE 0 END HAS_IPTV" +
      " FROM " + allTable + " WHERE ACCT_NBR !='' OR ACCT_NBR != null"
    val ddata = spark.sql(sql)
    ddata.createOrReplaceTempView(allTable)
  }

  /**
   * fee计算总费用
   */
  def cleanFeeData(feeDir: String, feeTable: String): String = {
    val feeData = loadData(feeDir, CrmConf.feeField, CrmConf.splitstr)
    feeData.createOrReplaceTempView(feeTable)
    val sql = "SELECT USER_ID,SUM(FEE) AS FEE FROM " +
      feeTable + " GROUP BY USER_ID"
    val ddata = spark.sql(sql)
    LOG.info("---------" + feeDir + " ---cleanFeeData show-------------------")
    ddata.show()
    val feeTableG = feeTable + "_GROUP"
    ddata.createOrReplaceTempView(feeTableG)
    feeTableG
  }

  /**
   * remove数据和上个月crm数据关联，获取crm完整数据，返回
   */
  def getRomoveData(startMonth: String, lastMonth: String, dataSourceType: String) = {
    val removeDir = CrmConf.newDir + "/" + dataSourceType + "/" + CrmConf.removeDir + "/" + startMonth
    val removeTable = "CRM_REMOVE_" + startMonth
    val removeData = loadData(removeDir, CrmConf.removeField, CrmConf.splitstr).createOrReplaceTempView(removeTable)
    //取上个月all fee 数据
    val lastCrmTable = "CRM_LAST_DATA_" + lastMonth
    val lastData = getAllFeeCrmInfo(lastMonth, dataSourceType).createOrReplaceTempView(lastCrmTable)
    /**
     * USER_ID,USER_NAME,EPARCHY_NAME,CUST_NAME,INSTALL_ADRESS,CHARGE_TYPE,FEE_VALUE,BEGIN_TIME,
     * END_TIME,ORDER_TYPE,ORDER_TIME,LINK_PHONE,CITY_CODE,UU_START,UU_END,SPEED,HAS_IPTV
     */
    val sql = "SELECT t2.USER_ID,t2.USER_NAME,t2.EPARCHY_NAME,t2.CUST_NAME,t2.INSTALL_ADRESS," +
      "t2.CHARGE_TYPE,t2.FEE_VALUE,t2.BEGIN_TIME, " +
      "t2.END_TIME,t2.ORDER_TYPE,t2.ORDER_TIME,t2.LINK_PHONE,t2.CITY_CODE," +
      "t2.UU_START,t2.UU_END,t2.SPEED,t2.HAS_IPTV" +
      " FROM " + removeTable + " t1  , " + lastCrmTable +
      " t2 WHERE t1.USER_ID = t2.USER_ID AND t1.ACCT_NBR = t2.USER_NAME AND t1.EPARCHY_CODE = t2.CITY_CODE"
    val data = spark.sql(sql)
    data
  }

  def main(args: Array[String]): Unit = {
    val dataSourceTypeArr = CrmConf.dataSourceType.split(",")
    //数据分类
    LOG.info("数据分类开始,start classify the  data")
    classifyCrmData(dataSourceTypeArr)
    LOG.info("数据分类结束,end classify the  data")
    var startMonth = TimeTool.beforeMonth(0)
    if (args.length >= 1) {
      startMonth = args(0)
    }
    LOG.info("处理数据开始--" + startMonth + ",start process the " + startMonth + " data")
    dealCrm(startMonth, dataSourceTypeArr)
    LOG.info("处理数据结束--" + startMonth + ",end process the " + startMonth + " data")
  }

}