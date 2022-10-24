import org.apache.spark.sql.{ DataFrame, Dataset, SparkSession, SaveMode }
import com.ainsg.lwfx.common.Spark._
import com.ainsg.lwfx.common.Spark.spark.implicits._
import com.ainsg.lwfx.features.config.FeaturesConf
object Test4 {
  def main(args: Array[String]): Unit = {
    //    val spark = SparkSession.builder().appName("data set example")
    //      .master("local").getOrCreate()
    //    import spark.implicits._
    //    月数据
    val x1 = Array(
      ("8010005111111111", "01", "201712", 3, 13.44, 1001, 332, 3, 5, "2017-12-01"),
      ("801010522222", "01", "201712", 5, 13.44, 1002, 325, 6, 3, "2017-09-11"),
      ("8010205", "01", "201712", 33, 18.44, 1004, 325, 6, 33, "2017-12-01"),
      ("8010006", "01", "201712", 332, 18.44, 1004, 3, 23, 35, "2017-08-11"),
      ("80145005", "01", "201712", 66, 18.44, 1004, 25, 66, 2, "2017-09-01"),
      ("8010008", "01", "201712", 10, 18.44, 1004, 335, 5, 63, "2017-06-01"),
      ("80100035", "01", "201712", 37, 18.44, 1004, 35, 66, 36, "2017-06-14"),
      ("8010002", "01", "201712", 8, 18.44, 1004, 36, 6, 78, "2017-12-11"),
      ("8010205", "01", "201712", 20, 5.67, 1005, 33, 6, 664, "2017-06-21"))
    val features1 = spark.createDataset(x1).toDF("USER_NAME", "CITY_CODE", "COLLECT_CYCLE",
      "DIAL_SUM", "TIMELEN_SUM", "OUTOCTETS_SUM", "INOCTETS_SUM", "OUTPACKETS_SUM", "INPACKETS_SUM", "LAST_USE_TIME")
    features1.show()
    features1.createTempView("beforeMonthData")
    //    val data = spark.sql("SELECT USER_NAME,(datediff('2018-01-01',max(cast(LAST_USE_TIME as String)))*24) as UNUSE_TIME FROM beforeMonthData GROUP BY USER_NAME")
    val data = spark.sql("SELECT USER_NAME,(datediff('2018-02-01',max(cast(LAST_USE_TIME as String)))*24) as UNUSE_TIME  FROM beforeMonthData GROUP BY USER_NAME")
    data.show()
  }
}