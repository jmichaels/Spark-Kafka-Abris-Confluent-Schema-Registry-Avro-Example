#class_name=SimpleDataframeExample
class_name=WriteToTopicWithAbris

spark-submit \
  --class $class_name \
  target/spark_abris_confluent_example-1.0-SNAPSHOT-uber.jar