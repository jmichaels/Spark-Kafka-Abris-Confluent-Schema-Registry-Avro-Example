import org.apache.spark.sql.SparkSession

object SimpleDataframeExample {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .appName("Simple DataFrame Example")
      .master("local[*]")
      .getOrCreate()

    val df = spark
      .read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("./data/Metro_Bike_Share_Trip_Data.csv")

    df.printSchema()
  }
}