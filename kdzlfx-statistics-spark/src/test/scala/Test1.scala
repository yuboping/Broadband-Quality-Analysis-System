import com.ainsg.lwfx.common.Spark

object Test1 {
  val spark = Spark.spark
  import spark.implicits._
  def main(args: Array[String]): Unit = {
    spark.read.format("csv")
      .option("header", "true") //csv第一行有属性的话"true"，没有就是"false"
      .option("inferSchema", true.toString) //自动推断属性列的数据类型。
      .load("F:/kdzlfx/statistic/userdet/201907/0001")
      //    spark.read.format("text").load("/Users/lee/Documents/test").as[String]
      //      .filter(row=>row.split(",").length==3)
      .show()
  }
}
