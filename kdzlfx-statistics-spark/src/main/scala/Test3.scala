import org.apache.spark.sql.{ DataFrame, Dataset, SparkSession, SaveMode }
import com.ainsg.lwfx.common.Spark._
import com.ainsg.lwfx.common.Spark.spark.implicits._
import com.ainsg.lwfx.features.config.FeaturesConf
object Test3 {
  def main(args: Array[String]): Unit = {
    //    val spark = SparkSession.builder().appName("data set example")
    //      .master("local").getOrCreate()
    //    import spark.implicits._
    //    月数据
    val x1 = Array(
      ("8010005111111111", "01", "201801", 3, 13.44, 1001, 332, 3, 5, "2017-06-01"),
      ("801010522222", "01", "201801", 5, 13.44, 1002, 325, 6, 3, "2017-06-01"),
      ("8010005", "01", "201801", 33, 18.44, 1004, 325, 6, 33, "2017-06-01"),
      ("8010006", "01", "201801", 332, 18.44, 1004, 3, 23, 35, "2017-06-01"),
      ("80145005", "01", "201712", 66, 18.44, 1004, 25, 66, 2, "2017-06-01"),
      ("8010008", "01", "201801", 10, 18.44, 1004, 335, 5, 63, "2017-06-01"),
      ("80100035", "01", "201801", 37, 18.44, 1004, 35, 66, 36, "2017-06-01"),
      ("8010002", "01", "201801", 8, 18.44, 1004, 36, 6, 78, "2017-06-01"),
      ("8010205", "01", "201801", 20, 5.67, 1005, 33, 6, 664, "2017-06-01"))
    val features1 = spark.createDataset(x1).toDF("USER_NAME", "CITY_CODE", "COLLECT_CYCLE",
      "DIAL_SUM", "TIMELEN_SUM", "OUTOCTETS_SUM", "INOCTETS_SUM", "OUTPACKETS_SUM", "INPACKETS_SUM", "LAST_USE_TIME")
    features1.show()
    features1.write.option("header", true).mode(SaveMode.Overwrite).save(FeaturesConf.monthStatisticPath + "/201801/01")
    val x2 = Array(
      ("8010005111111111", "01", "201712", 3, 13.44, 1001, 332, 3, 5, "2017-06-01"),
      ("801010522222", "01", "201712", 5, 13.44, 1002, 325, 6, 3, "2017-06-01"),
      ("8010005", "01", "201712", 33, 18.44, 1004, 325, 6, 33, "2017-06-01"),
      ("8010006", "01", "201712", 332, 18.44, 1004, 3, 23, 35, "2017-06-01"),
      ("80145005", "01", "201712", 66, 18.44, 1004, 25, 66, 2, "2017-06-01"),
      ("8010008", "01", "201712", 10, 18.44, 1004, 335, 5, 63, "2017-06-01"),
      ("80100035", "01", "201712", 37, 18.44, 1004, 35, 66, 36, "2017-06-01"),
      ("8010002", "01", "201712", 8, 18.44, 1004, 36, 6, 78, "2017-06-01"),
      ("8010205", "01", "201712", 20, 5.67, 1005, 33, 6, 664, "2017-06-01"))
    val features2 = spark.createDataset(x2).toDF("USER_NAME", "CITY_CODE", "COLLECT_CYCLE",
      "DIAL_SUM", "TIMELEN_SUM", "OUTOCTETS_SUM", "INOCTETS_SUM", "OUTPACKETS_SUM", "INPACKETS_SUM", "LAST_USE_TIME")
    features2.show()
    features2.write.option("header", true).mode(SaveMode.Overwrite).save(FeaturesConf.monthStatisticPath + "/201712/01")
    //    //周数据
    //    val x3 = Array(
    //      ("8010005111111111", "01", "201801", 32, 13.44, 3, 332, 35, 5),
    //      ("801010522222", "01", "201801", 31, 13.44, 1002, 325, 6, 3),
    //      ("8010005", "01", "201801", 12, 3.2, 1004, 325, 6, 33),
    //      ("8010006", "01", "201802", 52, 18.44, 355, 3, 23, 32),
    //      ("80145005", "01", "201803", 88, 18.44, 23, 25, 66, 2),
    //      ("8010008", "01", "201803", 10, 18.44, 1004, 33, 5, 13),
    //      ("80100035", "01", "201803", 44, 18.44, 2212, 35, 66, 36),
    //      ("8010002", "01", "201804", 42, 18.44, 1004, 36, 6, 78),
    //      ("8010205", "01", "201804", 7, 5.67, 1005, 33, 6, 664))
    //    val features_w1 = spark.createDataset(x3).toDF("USER_NAME", "CITY_CODE", "COLLECT_CYCLE",
    //      "DIAL_SUM", "TIMELEN_SUM", "OUTOCTETS_SUM", "INOCTETS_SUM", "OUTPACKETS_SUM", "INPACKETS_SUM")
    //    features_w1.show()
    //    features_w1.write.option("header", true).mode(SaveMode.Overwrite).save(FeaturesConf.weekStatisticPath + "/201801/01")
    //    val x4 = Array(
    //      ("8010005111111111", "01", "201751", 1, 13.44, 3, 332, 35, 5),
    //      ("801010522222", "01", "201753", 22, 13.44, 1002, 325, 6, 3),
    //      ("8010005", "01", "201754", 12, 3.2, 1004, 325, 6, 33),
    //      ("8010006", "01", "201751", 52, 18.44, 355, 3, 23, 32),
    //      ("80145005", "01", "201751", 22, 18.44, 23, 25, 66, 2),
    //      ("8010008", "01", "201750", 14, 18.44, 1004, 33, 5, 13),
    //      ("80100035", "01", "201752", 44, 18.44, 2212, 35, 66, 36),
    //      ("8010002", "01", "201752", 42, 18.44, 1004, 36, 6, 78),
    //      ("8010205", "01", "201751", 7, 5.67, 1005, 33, 6, 664))
    //    val features_w2 = spark.createDataset(x4).toDF("USER_NAME", "CITY_CODE", "COLLECT_CYCLE",
    //      "DIAL_SUM", "TIMELEN_SUM", "OUTOCTETS_SUM", "INOCTETS_SUM", "OUTPACKETS_SUM", "INPACKETS_SUM")
    //    features_w2.show()
    //    features_w2.write.option("header", true).mode(SaveMode.Overwrite).save(FeaturesConf.weekStatisticPath + "/201712/01")
    //订购关系
    //    val x5 = Array(
    //      ("8010005111111111", "801", 1),
    //      ("801010522222", "801", 2),
    //      ("8010005", "801", 3),
    //      ("8010006", "801", 3),
    //      ("80145005", "801", 3),
    //      ("8010008", "801", 3),
    //      ("80100035", "801", 1),
    //      ("8010002", "01", 1),
    //      ("8010205", "01", 3))
    //    val subscribe1 = spark.createDataset(x5).toDF("BMS_USER_NAME", "BMS_SVC_ID", "PS_OPERATE_TYPE")
    //    subscribe1.show()
    //    subscribe1.write.option("header", true).mode(SaveMode.Overwrite).csv(FeaturesConf.provHistoryPath + "/201801/01")
    //    val x6 = Array(
    //      ("8010005111111111", "801", 1),
    //      ("801010522222", "801", 2),
    //      ("8010005", "801", 1),
    //      ("8010006", "801", 3),
    //      ("80145005", "801", 2),
    //      ("8010008", "801", 1),
    //      ("80100035", "801", 3),
    //      ("8010002", "01", 1),
    //      ("8010205", "801", 3))
    //    val subscribe2 = spark.createDataset(x6).toDF("BMS_USER_NAME", "BMS_SVC_ID", "PS_OPERATE_TYPE")
    //    subscribe2.show()
    //    subscribe2.write.option("header", true).mode(SaveMode.Overwrite).csv(FeaturesConf.provHistoryPath + "/201712/01")
  }
}