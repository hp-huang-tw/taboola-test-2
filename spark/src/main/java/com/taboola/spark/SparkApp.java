package com.taboola.spark;

import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.DataTypes;

import java.util.concurrent.TimeUnit;

import static org.apache.spark.sql.functions.*;
import static org.apache.spark.sql.functions.col;

public class SparkApp {

    public static void main(String[] args) throws StreamingQueryException {
        SparkSession spark = SparkSession.builder().master("local[4]").getOrCreate();

        // generate events
        // each event has an id (eventId) and a timestamp
        // an eventId is a number between 0 an 99
        Dataset<Row> events = getEvents(spark);
//        events.printSchema();
//        events.writeStream()
//                .format("console")
//                .trigger(Trigger.ProcessingTime(10, TimeUnit.SECONDS))
//                .start();

        // REPLACE THIS CODE
        // The spark stream continuously receives messages. Each message has 2 fields:
        // * timestamp
        // * event id (valid values: from 0 to 99)
        //
        // The spark stream should collect, in the database, for each time bucket and event id, a counter of all the messages received.
        // The time bucket has a granularity of 1 minute.

        // saveEventLog(events);

        Dataset<Row> eventCount = events.withWatermark("timestamp", "1 minutes")
                .groupBy(window(col("timestamp"), "1 minutes").alias("time_range"),
                        col("eventId").alias("event_id"))
                .count();
//        eventCount.printSchema();
//        eventCount.writeStream()
//                    .outputMode("complete")
//                    .format("console")
//                    .option("truncate", "false")
//                    .option("numRows", "15")
//                    .trigger(Trigger.ProcessingTime(1, TimeUnit.MINUTES))
//                    .start();

        Dataset<Row> result = eventCount.select(col("time_range.start").alias("time_bucket"),
                col("event_id"), col("count"));
//        result.printSchema();

//        result.writeStream()
//                .outputMode("complete")
//                .format("console")
//                .option("truncate", "false")
//                .option("numRows", "15")
//                .trigger(Trigger.ProcessingTime(1, TimeUnit.MINUTES))
//                .start();

        result.writeStream()
                .outputMode("complete")
                .format("append")
                .foreachBatch(getDatabaseHandler("event_log_count"))
                .trigger(Trigger.ProcessingTime(1, TimeUnit.MINUTES))
                .start();

        // the stream will run forever
        spark.streams().awaitAnyTermination();
    }

    private static VoidFunction2<Dataset<Row>, Long> getDatabaseHandler(String tableName) {
        return (dataset, batchId) -> dataset.write()
                .format("jdbc")
                .option("driver", "org.hsqldb.jdbc.JDBCDriver")
                .option("url", "jdbc:hsqldb:hsql://localhost/xdb")
                .option("dbtable", tableName)
                .option("user", "sa")
                .mode(SaveMode.Overwrite)
                .save();
    }

    private static void saveEventLog(Dataset<Row> eventLogs) {
        eventLogs.writeStream()
                .format("append")
                .foreachBatch(getDatabaseHandler("event_log"))
                .trigger(Trigger.ProcessingTime(10, TimeUnit.SECONDS))
                .start();
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
