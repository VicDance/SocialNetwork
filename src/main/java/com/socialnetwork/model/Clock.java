package com.socialnetwork.model;

import java.time.Instant;

@FunctionalInterface
public interface Clock {
    Instant now();

    static Clock system() {
        return Instant::now;
    }
}
