package com.taboola.api;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.taboola.api.eventlog.EvenLogCountRepository;
import com.taboola.api.eventlog.EventLogCountEntity;
import com.taboola.api.utils.TimeUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api")
public class Controller {

    private final EvenLogCountRepository repository;

    public Controller(EvenLogCountRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/currentTime")
    public long time() {
        return Instant.now().toEpochMilli();
    }

    @GetMapping("/counters/time/{time}")
    public Map<String, Long> getByTimeBucket(@PathVariable(name = "time") Long time) {
        Instant timeBucket = TimeUtil.parseDataTime(time);
        List<EventLogCountEntity> entities = repository.findAllByTimeBucket(timeBucket);

        if (entities.isEmpty()) {
            return new HashMap<>();
        }

        return entities.stream()
                .collect(Collectors.toMap(entity -> String.valueOf(entity.getEventId()),
                        EventLogCountEntity::getCount));
    }

    @GetMapping("/counters/time/{time}/eventId/{eventId}")
    public Long getByTimeBucketAndEventId(@PathVariable(name = "time") Long time,
                                          @PathVariable(name = "eventId") Long eventId) {
        Instant timeBucket = TimeUtil.parseDataTime(time);
        Optional<EventLogCountEntity> entity = repository.findAllByTimeBucketAndEventId(timeBucket, eventId);

        return entity.map(EventLogCountEntity::getCount).orElse(0L);
    }

}
