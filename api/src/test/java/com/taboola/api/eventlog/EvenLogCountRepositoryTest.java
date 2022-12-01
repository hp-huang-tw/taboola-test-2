package com.taboola.api.eventlog;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest()
class EvenLogCountRepositoryTest {

    @Autowired
    private EvenLogCountRepository repository;

    @Test
    void findByTimeBucket() {
        Instant time = Instant.now();
        EventLogCountEntity entity = EventLogCountEntity.builder()
                .timeBucket(time)
                .eventId(3L)
                .count(1L)
                .build();
        EventLogCountEntity saved = repository.save(entity);
        List<EventLogCountEntity> result = repository.findAllByTimeBucket(time);

        List<EventLogCountEntity> expected = new ArrayList<>();
        expected.add(saved);

        assertThat(result).isEqualTo(expected);
    }
}
