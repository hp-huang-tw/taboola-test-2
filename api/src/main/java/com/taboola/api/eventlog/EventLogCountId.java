package com.taboola.api.eventlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.Instant;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLogCountId implements Serializable {

    @Column(name = "TIME_BUCKET", nullable = false)
    private Instant timeBucket;

    @Column(name = "EVENT_ID", nullable = false)
    private Long eventId;
}
