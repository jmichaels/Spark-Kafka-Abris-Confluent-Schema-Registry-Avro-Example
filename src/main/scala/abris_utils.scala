import org.apache.spark.deploy.SparkSubmit
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import za.co.absa.abris.avro.functions.to_avro
import org.apache.spark.sql.Dataset

object AbrisUtils {
  def readFromTopic() {

  }

  def writeToTopic(
      spark: SparkSession,
      df: Dataset[],
      dfSchema: StructType,
      topicName: String,
      kafkaBootstrapServers: String,
      schemaRegistryUrl: String
    ) {

    val allColumns = struct(df.columns.head, df.columns.tail: _*)

    var abrisConfig = AbrisConfig
      .toConfluentAvro
      .downloadSchemaByLatestVersion
      .andTopicNameStrategy(topicName)
      .usingSchemaRegistry(schemaRegistryUrl)
    

    val avroFrame = df.select(to_avro(allColumns, abrisConfig) as 'value).cache()

    // avroFrame.printSchema()
    // avroFrame.show(100, false)

    avroFrame
      .write
      .format("kafka")
      .option("kafka.bootstrap.servers", bootstrapServers)
      .option("topic", topicName)
      .save()
  }
}