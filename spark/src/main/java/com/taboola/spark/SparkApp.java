package com.taboola.spark;

import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.DataTypes;
import scala.Function2;
import scala.runtime.BoxedUnit;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class SparkApp {

    public static void main(String[] args) throws StreamingQueryException {
        SparkSession spark = SparkSession.builder().master("local[4]").getOrCreate();

        // generate events
        // each event has an id (eventId) and a timestamp
        // an eventId is a number between 0 an 99
        Dataset<Row> events = getEvents(spark);
        events.printSchema();

        // REPLACE THIS CODE
        // The spark stream continuously receives messages. Each message has 2 fields:
        // * timestamp
        // * event id (valid values: from 0 to 99)
        //
        // The spark stream should collect, in the database, for each time bucket and event id, a counter of all the messages received.
        // The time bucket has a granularity of 1 minute.

        events.writeStream()
                .format("console")
                .trigger(Trigger.ProcessingTime(10, TimeUnit.SECONDS))
                .start();

        VoidFunction2<Dataset<Row>, Long> batchHandler = (dataset, batchId) -> {
            dataset.write()
                    .format("jdbc")
                    .option("driver", "org.hsqldb.jdbc.JDBCDriver")
                    .option("url", "jdbc:hsqldb:hsql://localhost/xdb")
                    .option("dbtable", "event_log")
                    .option("user", "sa")
                    .insertInto("event_log");
//                    .mode(SaveMode.Append)
//                .save();

//            Properties connectionProperties = new Properties();
//            connectionProperties.put("driver", "org.hsqldb.jdbc.JDBCDriver");
//            connectionProperties.put("user", "sa");
//
//            dataset.write()
//                    .jdbc("jdbc:hsqldb:hsql://localhost/xdb", "event_log", connectionProperties);


        };

        events.writeStream()
                .format("append")
                .foreachBatch(batchHandler)
                .trigger(Trigger.ProcessingTime(10, TimeUnit.SECONDS))
                .start();

        // the stream will run forever
        spark.streams().awaitAnyTermination();
    }

    private static Dataset<Row> getEvents(SparkSession spark) {
        return spark
                .readStream()
                .format("rate")
                //.option("rowsPerSecond", "10000")
                .option("rowsPerSecond", "10")
                .load()
                .withColumn("eventId", functions.rand(System.currentTimeMillis()).multiply(functions.lit(100)).cast(DataTypes.LongType))
                .select("eventId", "timestamp");
    }

}
