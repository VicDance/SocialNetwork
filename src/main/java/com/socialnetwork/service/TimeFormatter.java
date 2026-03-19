package com.socialnetwork.service;

import com.socialnetwork.model.Clock;

import java.time.Duration;
import java.time.Instant;

public class TimeFormatter {

    private final Clock clock;

    public TimeFormatter(Clock clock) {
        this.clock = clock;
    }

    public String format(Instant postedAt) {
        Duration elapsed = Duration.between(postedAt, clock.now());
        long seconds = elapsed.toSeconds();

        if (seconds < 60) {
            return seconds == 1 ? "1 second ago" : seconds + " seconds ago";
        }
        long minutes = elapsed.toMinutes();
        if (minutes < 60) {
            return minutes == 1 ? "1 minute ago" : minutes + " minutes ago";
        }
        long hours = elapsed.toHours();
        if (hours < 24) {
            return hours == 1 ? "1 hour ago" : hours + " hours ago";
        }
        long days = elapsed.toDays();
        return days == 1 ? "1 day ago" : days + " days ago";
    }
}
