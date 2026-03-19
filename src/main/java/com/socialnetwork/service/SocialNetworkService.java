package com.socialnetwork.service;

import com.socialnetwork.model.Clock;
import com.socialnetwork.model.Message;
import com.socialnetwork.repository.FollowRepository;
import com.socialnetwork.repository.MessageRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SocialNetworkService {

    private final MessageRepository messageRepository;
    private final FollowRepository followRepository;
    private final TimeFormatter timeFormatter;
    private final Clock clock;

    public SocialNetworkService(
            MessageRepository messageRepository,
            FollowRepository followRepository,
            TimeFormatter timeFormatter,
            Clock clock) {
        this.messageRepository = messageRepository;
        this.followRepository = followRepository;
        this.timeFormatter = timeFormatter;
        this.clock = clock;
    }

    public void post(String author, String content) {
        messageRepository.save(new Message(author, content, clock.now()));
    }

    public List<String> read(String author) {
        return messageRepository.findByAuthor(author)
                .stream()
                .map(msg -> msg.content() + " (" + timeFormatter.format(msg.postedAt()) + ")")
                .toList();
    }

    public void follow(String follower, String followee) {
        followRepository.save(follower, followee);
    }

    public List<String> wall(String user) {
        List<Message> allMessages = new ArrayList<>(messageRepository.findByAuthor(user));

        followRepository.findFollowees(user)
                .forEach(followee -> allMessages.addAll(messageRepository.findByAuthor(followee)));

        return allMessages.stream()
                .sorted(Comparator.comparing(Message::postedAt).reversed())
                .map(msg -> msg.author() + " - " + msg.content() + " (" + timeFormatter.format(msg.postedAt()) + ")")
                .toList();
    }
}
