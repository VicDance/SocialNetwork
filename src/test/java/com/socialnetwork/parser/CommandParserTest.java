package com.socialnetwork.parser;

import com.socialnetwork.command.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    private CommandParser parser;

    @BeforeEach
    void setUp() {
        parser = new CommandParser();
    }

    @Nested
    class PostCommand {

        @Test
        void shouldParsePostCommand() {
            Optional<Command> result = parser.parse("Alice -> I love the weather today");

            assertTrue(result.isPresent());
            assertInstanceOf(Command.Post.class, result.get());
            Command.Post post = (Command.Post) result.get();
            assertEquals("Alice", post.author());
            assertEquals("I love the weather today", post.content());
        }
    }

    @Nested
    class ReadCommand {

        @Test
        void shouldParseReadCommand() {
            Optional<Command> result = parser.parse("Alice");

            assertTrue(result.isPresent());
            assertInstanceOf(Command.Read.class, result.get());
            assertEquals("Alice", ((Command.Read) result.get()).author());
        }
    }

    @Nested
    class FollowCommand {

        @Test
        void shouldParseWhenFollowCommand() {
            Optional<Command> result = parser.parse("Charlie follows Alice");

            assertTrue(result.isPresent());
            assertInstanceOf(Command.Follow.class, result.get());
            Command.Follow follow = (Command.Follow) result.get();
            assertEquals("Charlie", follow.follower());
            assertEquals("Alice", follow.followee());
        }
    }

    @Nested
    class WallCommand {

        @Test
        void shouldParseWallCommand() {
            Optional<Command> result = parser.parse("Charlie wall");

            assertTrue(result.isPresent());
            assertInstanceOf(Command.Wall.class, result.get());
            assertEquals("Charlie", ((Command.Wall) result.get()).user());
        }
    }

    @Nested
    class InvalidInput {

        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        void shouldReturnEmptyWhenBlankInput(String input) {
            assertTrue(parser.parse(input).isEmpty());
        }

        @Test
        void shouldReturnEmptyWhenNullInput() {
            assertTrue(parser.parse(null).isEmpty());
        }
    }
}
