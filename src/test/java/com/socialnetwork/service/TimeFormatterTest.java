package com.socialnetwork.service;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeFormatterTest {

    private final Instant TIME = Instant.parse("2024-01-01T12:00:00Z");

    private TimeFormatter formatterWithFixedClock() {
        return new TimeFormatter(() -> TIME);
    }

    @Test
    void shouldFormatOneSecond() {
        Instant postedAt = TIME.minusSeconds(1);
        assertEquals("1 second ago", formatterWithFixedClock().format(postedAt));
    }

    @Test
    void shouldFormatMultipleSeconds() {
        Instant postedAt = TIME.minusSeconds(45);
        assertEquals("45 seconds ago", formatterWithFixedClock().format(postedAt));
    }

    @Test
    void shouldFormatOneMinute() {
        Instant postedAt = TIME.minusSeconds(60);
        assertEquals("1 minute ago", formatterWithFixedClock().format(postedAt));
    }

    @Test
    void shouldFormatMultipleMinutes() {
        Instant postedAt = TIME.minusSeconds(5 * 60);
        assertEquals("5 minutes ago", formatterWithFixedClock().format(postedAt));
    }

    @Test
    void shouldFormatOneHour() {
        Instant postedAt = TIME.minusSeconds(3600);
        assertEquals("1 hour ago", formatterWithFixedClock().format(postedAt));
    }

    @Test
    void shouldFormatMultipleHours() {
        Instant postedAt = TIME.minusSeconds(3 * 3600);
        assertEquals("3 hours ago", formatterWithFixedClock().format(postedAt));
    }

    @Test
    void shouldFormatOneDay() {
        Instant postedAt = TIME.minusSeconds(86400);
        assertEquals("1 day ago", formatterWithFixedClock().format(postedAt));
    }

    @Test
    void shouldFormatMultipleDays() {
        Instant postedAt = TIME.minusSeconds(3 * 86400);
        assertEquals("3 days ago", formatterWithFixedClock().format(postedAt));
    }
}
