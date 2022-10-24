import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrameReader
import org.apache.spark.sql.functions._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.DataFrameStatFunctions
object Test {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("data set example")
      .master("local").getOrCreate()
    import spark.implicits._

    //所有用户,初始状态status=-1，status_m=-1
    val features = spark.createDataset(Seq(("aaa", -1, -1), ("bbb", -1, -1), ("ccc", -1, -1), ("ddd", -1, -1), ("ee", -1, -1), ("ff", -1, -1), ("gg", -1, -1), ("hh", -1, -1), ("ii", -1, -1), ("jj", -1, -1), ("kk", -1, -1))).toDF("USER_NAME", "STUTAS", "STUTAS_M")
    val allUser = features.selectExpr("USER_NAME", "-1 as STATUS")
    val wholeUser = spark.createDataset(Seq(("aaa", 0), ("bbb", 0), ("ccc", 0), ("ddd", 0), ("ee", 0), ("ff", 0), ("gg", 0), ("hh", 0), ("jj", 0), ("kk", 0))).toDF("USER_NAME", "STATUS1")
    //    话单完整的用户：0
    //    周期第一个月新开用户：-2
    val openUser = spark.createDataset(Seq(("aaa", -2), ("kk", -2))).toDF("USER_NAME", "STATUS2")
    //    周期最后一个月销户用户：-3
    val cancelUser = spark.createDataset(Seq(("ee", -3))).toDF("USER_NAME", "STATUS3")
    val fulldata = allUser.join(wholeUser, Seq("USER_NAME"), "left")
      .join(openUser, Seq("USER_NAME"), "left")
      .join(cancelUser, Seq("USER_NAME"), "left")
      .show()
    //    val abc = allUser.map(row => {
    //      var username = row.getString(0)
    //      var status = row.getInt(1)
    //      var status_m = row.getInt(2)
    //      if (wholeUser.value.filter("USER_NAME='" + username + "'").count() > 0) {
    //        if (openUser.value.filter("USER_NAME='" + username + "'").count() > 0) { //话单完整并且是周期第一个月新开的用户（用户特征不规律，需剔除不作为训练或预测用户），状态status=-1，status_m=-2
    //          status_m = -2
    //        } else if (cancelUser.value.filter("USER_NAME='" + username + "'").count() > 0) { //话单完整并且是周期最后一个月销户的用户（已销户，需剔除不作为训练或预测用户），状态status=-1，status_m=-3
    //          status_m = -3
    //        } else { //话单完整且不是新开或者销户的用户
    //          status = 0
    //          status_m = 0
    //        }
    //      }
    //      (username, status, status_m)
    //    })
    //    abc.toDF("USER_NAME", "STUTAS", "STUTAS_M").show()
    //    allUser.show()
    //      (arr(0), arr(1), arr(2), arr(3), arr(4))  )
    //      var username = row.getString(0)
    //      if (wholeUser.filter("USER_NAME='" + username + "'").count() > 0) {
    //        if (openUser.filter("USER_NAME='" + username + "'").count() > 0) {
    //          row.
    //          row(2) = -1
    //          row(3) = -2
    //        }
    //      }
    //    })

    //    val aa = "aaa"
    //    println(openUser.filter("USER_NAME='" + aa + "'").count())

    //    val data = Seq(("aaa", "201711", 1, 2, -1), ("bbb", "201711", 3, 4, -1), ("ccc", "201711", 3, 5, -1), ("ddd", "201711", 4, 6, -1),
    //      ("aaa", "201712", 1, 2, -1), ("bbb", "201712", 3, 4, -1), ("ccc", "201712", 3, 5, -1),
    //      ("aaa", "201710", 1, 2, -1), ("ccc", "201710", 3, 5, -1), ("eee", "201711", 3, 4, -1), ("eee", "201710", 3, 4, -1), ("eee", "201712", 3, 4, -1))
    //
    //    val showData = spark.createDataset(data).toDF("USER_NAME", "STOP_TIME_M", "DIAL_SUM", "TIMELEN_SUM", "STATUS")
    //    val startDay = "201801"
    //    val pathList = Array("201712", "201711", "201710")
    //    showData.createTempView("showData")
    //    //    话单齐全
    //    val fullDetail = spark.sql("select USER_NAME,'0' as STATUS_M from (select distinct USER_NAME,STOP_TIME_M from showData) group by USER_NAME having count(*)>=3")
    //    val openUser = spark.createDataset(Seq(("aaa", "-2"), ("bbb", "-2"))).toDF("USER_NAME", "STATUS_M")
    //    val cancelUser = spark.createDataset(Seq(("ddd", "-3"), ("bbb", "-3"))).toDF("USER_NAME", "STATUS_M")
    //    fullDetail.show()
    //    val incalculable = openUser.union(cancelUser)
    //    incalculable.show()
    //    fullDetail.except(incalculable).show()
    //    aa.show()
    //    val bb = showData.intersect(aa)
    //    bb.show()
  }
}
//import org.apache.spark.sql.types._
//val schema = StructType(List(
//  StructField("integer_column", IntegerType, nullable = false),
//  StructField("string_column", StringType, nullable = true),
//  StructField("date_column", DateType, nullable = true)))
//
//val rdd = sc.parallelize(Seq(
//  Row(1, "First Value", java.sql.Date.valueOf("2010-01-01")),
//  Row(2, "Second Value", java.sql.Date.valueOf("2010-02-01"))))
//val df = sqlContext.createDataFrame(rdd, schema)