package com.socialnetwork.repository;

import com.socialnetwork.model.Message;

import java.util.List;

public interface MessageRepository {

    void save(Message message);
    List<Message> findByAuthor(String author);
}
