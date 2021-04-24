from pyspark.sql import SparkSession

spark = SparkSession\
    .builder\
    .appName("JVM Bridge Example")\
    .getOrCreate()

spark.sparkContext._jvm.JvmBridgeExample.helloWorld()