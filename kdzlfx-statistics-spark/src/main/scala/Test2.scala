import org.apache.spark.sql.{ DataFrame, Dataset, SparkSession }
import org.apache.spark.sql.types._
object Test2 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("data set example")
      .master("local").getOrCreate()
    import spark.implicits._
    val schema = StructType(List(
      StructField("integer_column", IntegerType, nullable = false),
      StructField("string_column", StringType, nullable = true),
      StructField("date_column", DateType, nullable = true)))
    //    val features = spark.createDataset(Seq(("aaa", -1, -1), ("bbb", -1, -1), ("ccc", -1, -1), ("ddd", -1, -1), ("ee", -1, -1), ("ff", -1, -1), ("gg", -1, -1), ("hh", -1, -1), ("ii", -1, -1), ("jj", -1, -1), ("kk", -1, -1))).toDF("USER_NAME", "NUM", "LENGTH")
    //    val allUser = features.selectExpr("USER_NAME")
    //    //    话单完整的用户：0
    //    val wholeUser = spark.createDataset(Seq(("aaa"), ("bbb"), ("ccc"), ("ddd"), ("ee"), ("ff"), ("gg"), ("hh"), ("jj"), ("kk"))).toDF("USER_NAME")
    //    //    周期第一个月新开用户：-2
    //    val openUser = spark.createDataset(Seq(("aaa"), ("kk"))).toDF("USER_NAME")
    //    //    周期最后一个月销户用户：-3
    //    val cancelUser = spark.createDataset(Seq(("ee"))).toDF("USER_NAME")
    //    //话单不完整用户状态
    //
    //    val incomplete = allUser.except(wholeUser).selectExpr("USER_NAME", "-1 AS STATUS", "-1 AS STATUS_M").show()
    //    val open = wholeUser.intersect(openUser).selectExpr("USER_NAME", "-1 AS STATUS", "-2 AS STATUS_M").show()
    //    val cancel = wholeUser.intersect(cancelUser).selectExpr("USER_NAME", "-1 AS STATUS", "-3 AS STATUS_M").show()
    //    val complete = wholeUser.except(openUser.union(cancelUser)).selectExpr("USER_NAME", "0 AS STATUS", "0 AS STATUS_M").show()
  }
}