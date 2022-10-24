import org.apache.spark.sql.SparkSession
import org.apache.hadoop.mapred.TextInputFormat
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrameReader
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.DataFrameStatFunctions
import com.ainsg.lwfx.transformer.DataColumns
object Test5 {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("data set example")
      .master("local").getOrCreate()
    import spark.implicits._
    var data: DataFrame = null
    val source = "F:\\download\\201803_3000_KDYH_1702_ALL.AVL"
    val fieldstr = "USER_ID,ACCT_NBR,EPARCHY_NAME,CUST_NAME,INSTALL_ADRESS,PRODUCT_NAME,BB_FEE,START_DATE,END_DATE,DISCNT_NAME,TRADE_DATE,LINK_PHONE,EPARCHY_CODE,UU_START,UU_END,SPEED,HAS_IPTV"
    val splitstr = "|"
    val rddData = spark.sparkContext.hadoopFile(source, classOf[TextInputFormat], classOf[LongWritable], classOf[Text])
      .map(p => new String(p._2.getBytes, 0, p._2.getLength, "GBK"))
    data = DataColumns.RDDToDF(spark, rddData, fieldstr, splitstr)
    data.show()
    data.createOrReplaceTempView("tset")
    val ddata = spark.sql("select ACCT_NBR,EPARCHY_NAME,CUST_NAME,INSTALL_ADRESS from tset")
    ddata.show()
    //    val data = Seq(("1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26"))
    //    val showData = spark.createDataset(data)
    //    showData.show()
    //    val rddata = showData.filter(row => row.count(_ == '|') == 25)
    //    val newdata = rddata.rdd.map(_.split("\\|", 26)).map(k => {
    //      for (i <- 0 to (k.length - 1)) {
    //        k(i) = k(i).trim()
    //      }
    //      k
    //    }).map(h => Row.fromSeq(h.toSeq))
    //    newdata.foreach(println(_))
    //    val schemaString = "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z"
    //    val columnArray = schemaString.split(",")
    //    val fields = columnArray.map(fieldName => StructField(fieldName, StringType, nullable = true))
    //    val schema = StructType(fields)
    //    val peopleDF = spark.createDataFrame(newdata, schema)
    //    peopleDF.show()
    //    peopleDF.createOrReplaceTempView("tset")
    //    val ddata = spark.sql("select a,x,y,z from tset")
    //    ddata.show()
    //    ddata.createOrReplaceTempView("tt")
    //    val bb = spark.sql("select * from tset t1 left join tt t2 where t1.a=t2.a")
    //    bb.show()
    //    println("end")
  }

}
