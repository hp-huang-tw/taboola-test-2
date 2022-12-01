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
@Table(name = "event_log_count",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = { "time_bucket", "event_id" })})
@Builder
public class EventLogCountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "time_bucket", nullable = false)
    private Instant timeBucket;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "event_count", nullable = false)
    private long count;

}
