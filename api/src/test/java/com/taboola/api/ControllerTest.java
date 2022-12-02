package com.taboola.api;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.taboola.api.eventlog.EvenLogCountRepository;
import com.taboola.api.eventlog.EventLogCountEntity;
import com.taboola.api.utils.TimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;


@WebMvcTest(Controller.class)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvenLogCountRepository repository;

    @Test
    void time() throws Exception {
        mockMvc.perform(get("/api/currentTime")).andExpect(status().isOk());
    }

    @Test
    void getByTimeBucket() throws Exception {
        final Instant timeBucket = LocalDateTime.of(2022, 12, 12, 1, 59, 0)
                .atZone(TimeUtil.ZONE_ID_TW).toInstant();
        ArrayList<EventLogCountEntity> entities = new ArrayList<>();
        entities.add(EventLogCountEntity.builder()
                        .timeBucket(timeBucket)
                        .eventId(1L)
                        .count(1L)
                .build());
        entities.add(EventLogCountEntity.builder()
                .timeBucket(timeBucket)
                .eventId(2L)
                .count(2L)
                .build());
        given(repository.findAllByTimeBucket(timeBucket))
                .willReturn(entities);

        mockMvc.perform(get("/api/counters/time/202212120159"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("1").value(1L))
                .andExpect(jsonPath("2").value(2L));
    }

    @Test
    void getByTimeBucket_emptyResult() throws Exception {
        final Instant timeBucket = LocalDateTime.of(2022, 12, 12, 1, 59, 0)
                .atZone(TimeUtil.ZONE_ID_TW).toInstant();
        ArrayList<EventLogCountEntity> entities = new ArrayList<>();

        given(repository.findAllByTimeBucket(timeBucket))
                .willReturn(entities);

        mockMvc.perform(get("/api/counters/time/202212120159"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getByTimeBucketAndEventId_notFound() throws Exception {
        final Instant timeBucket = LocalDateTime.of(2022, 12, 12, 1, 59, 0)
                .atZone(TimeUtil.ZONE_ID_TW).toInstant();

        given(repository.findAllByTimeBucketAndEventId(timeBucket, 1L))
                .willReturn(Optional.empty());

        mockMvc.perform(get("/api/counters/time/202212120159/eventId/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}
