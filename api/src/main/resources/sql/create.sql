CREATE TABLE event_log_count (
    TIME_BUCKET TIMESTAMP NOT NULL,
    EVENT_ID int NOT NULL,
    EVENT_COUNT int NOT NULL,
    PRIMARY KEY(time_bucket, event_id)
);
