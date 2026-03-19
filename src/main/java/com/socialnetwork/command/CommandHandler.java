package com.socialnetwork.command;

import com.socialnetwork.service.SocialNetworkService;

import java.io.PrintStream;
import java.util.List;

public class CommandHandler {

    private final SocialNetworkService service;
    private final PrintStream out;

    public CommandHandler(SocialNetworkService service, PrintStream out) {
        this.service = service;
        this.out = out;
    }

    public void handle(Command command) {
        switch (command) {
            case Command.Post(var author, var content)    -> service.post(author, content);
            case Command.Read(var author)                 -> printLines(service.read(author));
            case Command.Follow(var follower, var followee) -> service.follow(follower, followee);
            case Command.Wall(var user)                   -> printLines(service.wall(user));
        }
    }

    private void printLines(List<String> lines) {
        lines.forEach(out::println);
    }
}
