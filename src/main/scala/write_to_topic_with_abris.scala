import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.struct
import org.apache.spark.sql.types._
import utils.AbrisUtils

object WriteToTopicWithAbris {
  def main(args: Array[String]): Unit = {
    
    val spark = SparkSession
      .builder
      .appName("WriteToTopicWithAbris")
      .master("local[*]")
      .getOrCreate()

    val schema = new StructType()
      .add("field1", IntegerType, false)
      .add("field2", DoubleType, false)
      .add("field3", StringType, false)

    val df = spark
      .read
      .option("multiline", true)
      .schema(schema)
      .json("data/abris_example_default_topic_schema.json")

    df.show()
    df.printSchema()

    //val kafkaTopicName = "abris-test"
    val schemaRegistryUrl = "http://localhost:8081"
    val bootstrapServers = "localhost:9092"

    // AbrisUtils.writeToTopic(dfToWrite: df, spark: spark, dfSchema: schema, kafkaTopicName: "abris-test")
    AbrisUtils.writeToTopic(
      dfToWrite = df,
      spark = spark,
      dfSchema = schema,
      kafkaTopicName = "abris-test"
    )

  }
}