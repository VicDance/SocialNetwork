package com.socialnetwork.parser;

import com.socialnetwork.command.Command;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    private static final Pattern POST_PATTERN   = Pattern.compile("^(\\S+)\\s+->\\s+(.+)$");
    private static final Pattern FOLLOW_PATTERN = Pattern.compile("^(\\S+)\\s+follows\\s+(\\S+)$");
    private static final Pattern WALL_PATTERN   = Pattern.compile("^(\\S+)\\s+wall$");
    private static final Pattern READ_PATTERN   = Pattern.compile("^(\\S+)$");

    public Optional<Command> parse(String input) {
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }

        String trimmed = input.trim();

        Matcher post = POST_PATTERN.matcher(trimmed);
        if (post.matches()) {
            return Optional.of(new Command.Post(post.group(1), post.group(2)));
        }

        Matcher follow = FOLLOW_PATTERN.matcher(trimmed);
        if (follow.matches()) {
            return Optional.of(new Command.Follow(follow.group(1), follow.group(2)));
        }

        Matcher wall = WALL_PATTERN.matcher(trimmed);
        if (wall.matches()) {
            return Optional.of(new Command.Wall(wall.group(1)));
        }

        Matcher read = READ_PATTERN.matcher(trimmed);
        if (read.matches()) {
            return Optional.of(new Command.Read(read.group(1)));
        }

        return Optional.empty();
    }
}
