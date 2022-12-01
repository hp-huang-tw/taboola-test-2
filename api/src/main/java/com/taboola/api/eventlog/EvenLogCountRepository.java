package com.taboola.api.eventlog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EvenLogCountRepository extends JpaRepository<EventLogCountEntity, EventLogCountId> {

    List<EventLogCountEntity> findAllByTimeBucket(Instant timeBucket);

    Optional<EventLogCountEntity> findAllByTimeBucketAndEventId(Instant timeBucket, Long eventId);
}
