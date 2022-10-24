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
import org.apache.hadoop.mapred.TextInputFormat
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
object Test6 {

  val spark = SparkSession.builder().appName("data set example")
    .master("local").getOrCreate()
  import spark.implicits._
  def main(args: Array[String]): Unit = {
    val aa = spark.sparkContext.hadoopFile("D:\\workspace\\gitrepository\\ces-statistics-spark\\src\\main\\resources\\test.txt", classOf[TextInputFormat], classOf[LongWritable], classOf[Text],
      1).map(p => {
        println(p._2)
        new String(p._2.getBytes, 0, p._2.getLength, "GBK")
      })
      .foreach(println(_))
  }
}
