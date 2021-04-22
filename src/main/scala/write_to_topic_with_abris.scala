import org.apache.spark.sql.SparkSession
import za.co.absa.abris.config.ToAvroConfig
import org.apache.spark.sql.functions.struct
import za.co.absa.abris.config.AbrisConfig
import org.apache.spark.sql.types._

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

    val kafkaTopicName = "abris-test"
    val schemaRegistryUrl = "http://localhost:8081"
    val bootstrapServers = "localhost:9092"


    val allColumns = struct(df.columns.head, df.columns.tail: _*)

    var abrisConfig = AbrisConfig
      .toConfluentAvro
      .downloadSchemaByLatestVersion
      .andTopicNameStrategy(kafkaTopicName)
      .usingSchemaRegistry(schemaRegistryUrl)
    
    import za.co.absa.abris.avro.functions.to_avro

    val avroFrame = df.select(to_avro(allColumns, abrisConfig) as 'value).cache()

    println("allColumns:")
    println(allColumns)

    avroFrame.printSchema()
    avroFrame.show(100, false)

    avroFrame
      .write
      .format("kafka")
      .option("kafka.bootstrap.servers", bootstrapServers)
      .option("topic", kafkaTopicName)
      .save()
  }
}