package com.socialnetwork.command;

public sealed interface Command permits
        Command.Post,
        Command.Read,
        Command.Follow,
        Command.Wall {

    record Post(String author, String content) implements Command {}
    record Read(String author) implements Command {}
    record Follow(String follower, String followee) implements Command {}
    record Wall(String user) implements Command {}
}
