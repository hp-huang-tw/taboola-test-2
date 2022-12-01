package com.taboola.api.eventlog;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;


@Data
public class EventLogCountId implements Serializable {

    private Instant timeBucket;

    private String eventId;
}
