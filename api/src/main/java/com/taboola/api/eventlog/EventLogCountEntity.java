package com.taboola.api.eventlog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(EventLogCountId.class)
@Table(name = "event_log_count")
@Builder
public class EventLogCountEntity {

    @Id
    @Column(name = "TIME_BUCKET", nullable = false)
    private Instant timeBucket;

    @Id
    @Column(name = "EVENT_ID", nullable = false)
    private Long eventId;

    @Column(name = "EVENT_COUNT", nullable = false)
    private Long count;

}
