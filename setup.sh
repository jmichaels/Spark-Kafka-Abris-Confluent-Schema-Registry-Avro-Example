kafka-topics --create --topic abris-test --bootstrap-server localhost:9092

echo "In Confluent Control Center, set this as the schema for the abris-test topic"


# "Nullability" attribute of each field is important.

# The nullability of your Schema Registry schema fields
# must match the nullability of your dataframe fields, or
# you will get a confusing error like:

# org.apache.avro.AvroRuntimeException: Not a union: "int"

# Unfortunately, setting nullable as "false" here, does
# not actually do anything.  The dataframe schema will still
# say the fields are "nullable: True".

# See:  https://stackoverflow.com/questions/41705602/spark-dataframe-schema-nullable-fields

# Not sure how to solve this at the moment, but for testing
# you can change the Schema Registry schema for this topic
# so that all fields allow null.

cat << EOF
{
    "doc": "Sample schema to help you get started.",
    "fields": [
    {
        "doc": "The int type is a 32-bit signed integer.",
        "name": "field1",
        "type": [
        "int",
        "null"
        ]
    },
    {
        "doc": "The double type is a double precision (64-bit) IEEE 754 floating-point number.",
        "name": "field2",
        "type": [
        "double",
        "null"
        ]
    },
    {
        "doc": "The string is a unicode character sequence.",
        "name": "field3",
        "type": [
        "string",
        "null"
        ]
    }
    ],
    "name": "value_abris_test",
    "namespace": "com.mycorp.mynamespace",
    "type": "record"
}
EOF