package utils

import org.apache.spark.deploy.SparkSubmit
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.functions.struct
import za.co.absa.abris.avro.functions.to_avro
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.DataFrame
import za.co.absa.abris.config.AbrisConfig
import za.co.absa.abris.config.ToAvroConfig

object AbrisUtils {
  val schemaRegistryUrl = "http://localhost:8081"
  val kafkaBootstrapServers = "localhost:9092"

  def readFromTopic() {

  }

  def writeToTopic(spark: SparkSession, dfToWrite: DataFrame, dfSchema: StructType, kafkaTopicName: String): Unit = {

    val allColumns = struct(dfToWrite.columns.head, dfToWrite.columns.tail: _*)

    var abrisConfig = AbrisConfig
      .toConfluentAvro
      .downloadSchemaByLatestVersion
      .andTopicNameStrategy(kafkaTopicName)
      .usingSchemaRegistry(schemaRegistryUrl)
    

    val avroFrame = dfToWrite.select(to_avro(allColumns, abrisConfig) as 'value).cache()

    // avroFrame.printSchema()
    // avroFrame.show(100, false)

    avroFrame
      .write
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBootstrapServers)
      .option("topic", kafkaTopicName)
      .save()
  }
}