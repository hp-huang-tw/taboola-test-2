package com.taboola.api.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.taboola.api.utils.TimeUtil.ZONE_ID_TW;
import static org.assertj.core.api.Assertions.assertThat;

class TimeUtilTest {

    @Test
    void parseDataTime() {
        assertThat(TimeUtil.parseDataTime(202212120159L))
                .isEqualTo(LocalDateTime.of(2022,12,12, 1, 59, 0)
                        .atZone(ZONE_ID_TW).toInstant());
    }
}
