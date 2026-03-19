package com.socialnetwork.repository;

import com.socialnetwork.model.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryMessageRepository implements MessageRepository {

    private final Map<String, List<Message>> store = new ConcurrentHashMap<>();

    @Override
    public void save(Message message) {
        store.computeIfAbsent(message.author(), nonUsed -> new ArrayList<>()).add(message);
    }

    @Override
    public List<Message> findByAuthor(String author) {
        return store.getOrDefault(author, List.of())
                .stream()
                .sorted(Comparator.comparing(Message::postedAt).reversed())
                .toList();
    }
}
