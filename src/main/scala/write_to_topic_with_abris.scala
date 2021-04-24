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

    // "Nullability" attribute of each field is important.
    //
    // The nullability of your Schema Registry schema fields
    // must match the nullability of your dataframe fields, or
    // you will get a confusing error like:
    //
    // org.apache.avro.AvroRuntimeException: Not a union: "int"
    //
    // Unfortunately, setting nullable as "false" here, does
    // not actually do anything.  The dataframe schema will still
    // say the fields are "nullable: True".
    //
    // See:  https://stackoverflow.com/questions/41705602/spark-dataframe-schema-nullable-fields
    //
    // Not sure how to solve this at the moment, but for testing
    // you can change the Schema Registry schema for this topic
    // so that all fields allow null:
    //
    // {
    //   "doc": "Sample schema to help you get started.",
    //   "fields": [
    //     {
    //       "doc": "The int type is a 32-bit signed integer.",
    //       "name": "field1",
    //       "type": [
    //         "int",
    //         "null"
    //       ]
    //     },
    //     {
    //       "doc": "The double type is a double precision (64-bit) IEEE 754 floating-point number.",
    //       "name": "field2",
    //       "type": [
    //         "double",
    //         "null"
    //       ]
    //     },
    //     {
    //       "doc": "The string is a unicode character sequence.",
    //       "name": "field3",
    //       "type": [
    //         "string",
    //         "null"
    //       ]
    //     }
    //   ],
    //   "name": "value_abris_test",
    //   "namespace": "com.mycorp.mynamespace",
    //   "type": "record"
    // }
    //
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