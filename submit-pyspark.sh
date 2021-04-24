job_file="jvm_bridge_example.py"
job_file="read_kafka_with_abris.py"

spark-submit \
  --driver-class-path target/spark_abris_confluent_example-1.0-SNAPSHOT-uber.jar \
  src/main/python/$job_file
