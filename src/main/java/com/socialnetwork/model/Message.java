package com.socialnetwork.model;

import java.time.Instant;

public record Message(String author, String content, Instant postedAt) {

    public Message {
        if (author == null || author.isBlank()) throw new IllegalArgumentException("Author must not be blank");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("Content must not be blank");
        if (postedAt == null) throw new IllegalArgumentException("PostedAt must not be null");
    }
}
