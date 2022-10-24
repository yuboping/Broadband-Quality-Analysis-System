package com.ainsg.lwfx.common

import org.apache.spark.sql.SparkSession
import com.ainsg.lwfx.util.UDF
import com.ainsg.lwfx.util.TimeTool
import com.ainsg.lwfx.config.SparkConf
import scala.reflect.api.materializeTypeTag
object Spark {
  val spark = {
    val s = SparkSession.builder()
      .appName(SparkConf.spark_name)
//      .master("local")
      .config("spark.sql.warehouse.dir", SparkConf.spark_sql_dir)
      .getOrCreate()
    s.udf.register("myDecode", UDF.myDecode(_: Int, _: Int, _: Int, _: Int))
    s.udf.register("mycrmdecode", UDF.mycrmdecode(_: String))
    s.udf.register("strToDateToStr", UDF.strToDateToStr(_: String, _: String, _: String))
    s.udf.register("strNullSetVal", UDF.strNullSetVal(_: String, _: String, _: String))
    s.udf.register("sixteenTransTen", UDF.sixteenTransTen(_: String))
    s.udf.register("tenTransSixteen", UDF.tenTransSixteen(_: Int))
    s.udf.register("addColon", UDF.addColon(_: String))
    s.udf.register("calcHealthValue", UDF.calcHealthValue(_: Int, _: Int, _: Int))
    s.udf.register("calcBQ", UDF.calcBQ(_: Int, _: Int, _: Int))
    s.udf.register("complaintHealth", UDF.complaintHealth(_: Int))
    s.udf.register("monthHour", TimeTool.monthHour(_: String))
    s
  }
}