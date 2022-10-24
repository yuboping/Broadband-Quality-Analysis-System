package com.ainsg.lwfx.transformer

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

object DataColumns {

  def RDDToDF(sparkSession: SparkSession, original: RDD[String], schemaString: String, regionNum: String = "0", split: String = ","): DataFrame = {
    //将分隔符的数据，转化为dataframe
    val columnArray = schemaString.split(",")
    val length = columnArray.length
    val fields = columnArray.map(fieldName => StructField(fieldName, StringType, nullable = true))
    val schema = StructType(fields)
    val rowRDD = original
      .map(_.split(split, columnArray.length)).filter(_.length == length).map(k => {
        for (i <- 0 to (k.length - 1)) {
          if (k(i) == null || k(i).equals("")) k(i) = "" else k(i) = k(i).trim()
          if (regionNum == "1" && i == 25) {
            k(i) = regionToAreaNo(k(i))
          }
        }
        k
      }).map(h => Row.fromSeq(h.toSeq)) //RDD[Row]
    val peopleDF = sparkSession.createDataFrame(rowRDD, schema)
    peopleDF
  }

  def regionToAreaNo(regionName: String): String = {
    var areaNo = ""
    if (regionName == "省中心") {
      areaNo = "00"
    } else if (regionName == "合肥") {
      areaNo = "0001"
    } else if (regionName == "芜湖") {
      areaNo = "0002"
    } else if (regionName == "蚌埠") {
      areaNo = "0003"
    } else if (regionName == "滁州") {
      areaNo = "0004"
    } else if (regionName == "安庆") {
      areaNo = "0005"
    } else if (regionName == "六安") {
      areaNo = "0006"
    } else if (regionName == "黄山") {
      areaNo = "0007"
    } else if (regionName == "宣城") {
      areaNo = "0008"
    } else if (regionName == "淮南") {
      areaNo = "0009"
    } else if (regionName == "宿州") {
      areaNo = "0010"
    } else if (regionName == "马鞍山") {
      areaNo = "0011"
    } else if (regionName == "铜陵") {
      areaNo = "0012"
    } else if (regionName == "淮北") {
      areaNo = "0013"
    } else if (regionName == "阜阳") {
      areaNo = "0014"
    } else if (regionName == "池州") {
      areaNo = "0015"
    } else if (regionName == "亳州") {
      areaNo = "0017"
    } else {
      areaNo = regionName
    }
    areaNo
  }
  
}
