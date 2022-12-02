package com.taboola.api.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeUtil {

    public static final ZoneId ZONE_ID_TW = ZoneId.of("Asia/Taipei");

    public static Instant parseDataTime(Long dateTime) {
        String stringDate = String.valueOf(dateTime);
        String pattern = "yyyyMMddHHmm";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.TAIWAN);
        LocalDateTime localDateTime = LocalDateTime.parse(stringDate, dateTimeFormatter);
        return localDateTime.atZone(ZONE_ID_TW).toInstant();
    }
}
