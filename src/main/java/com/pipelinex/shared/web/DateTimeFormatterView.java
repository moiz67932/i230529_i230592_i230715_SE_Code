package com.pipelinex.shared.web;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeFormatterView {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final ZoneId zoneId = ZoneId.systemDefault();

    public String date(LocalDate value) {
        return value == null ? "" : DATE.format(value);
    }

    public String date(Instant value) {
        return value == null ? "" : DATE.format(value.atZone(zoneId));
    }

    public String dateTime(Instant value) {
        return value == null ? "" : DATE_TIME.format(value.atZone(zoneId));
    }
}
