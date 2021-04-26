from pyspark.sql import SparkSession

spark = SparkSession\
    .builder\
    .appName("JVM Bridge Example")\
    .getOrCreate()

myName = "Someone"

spark.sparkContext._jvm.JvmBridgeExample.helloWorld()

spark.sparkContext._jvm.JvmBridgeExample.sayHello(myName)


warning = """

*** Notice ***

If a function is called with missing or incorrect arguments,
the error will be confusing.

It will say that the function "does not exist" (with the arg
types that you provided), instead of saying "incorrect arguments"

If you call a function that expects a "String", with an "Int":

"""

print(warning)

myName = 42

spark.sparkContext._jvm.JvmBridgeExample.sayHello(myName)