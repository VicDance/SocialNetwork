package com.socialnetwork.integration;

import com.socialnetwork.command.CommandHandler;
import com.socialnetwork.model.Clock;
import com.socialnetwork.parser.CommandParser;
import com.socialnetwork.repository.InMemoryFollowRepository;
import com.socialnetwork.repository.InMemoryMessageRepository;
import com.socialnetwork.service.SocialNetworkService;
import com.socialnetwork.service.TimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SocialNetworkIntegrationTest {
    private AtomicReference<Instant> clockRef;
    private CommandParser parser;
    private CommandHandler handler;
    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() {
        clockRef = new AtomicReference<>(Instant.parse("2024-01-01T12:00:00Z"));
        Clock clock = clockRef::get;
        var messageRepo = new InMemoryMessageRepository();
        var followRepo = new InMemoryFollowRepository();
        var formatter = new TimeFormatter(clock);
        var service = new SocialNetworkService(messageRepo, followRepo, formatter, clock);
        output = new ByteArrayOutputStream();
        handler = new CommandHandler(service, new PrintStream(output));
        parser = new CommandParser();
    }

    private void execute(String line) {
        parser.parse(line).ifPresent(handler::handle);
    }

    private void advanceBy(long seconds) {
        clockRef.updateAndGet(t -> t.plusSeconds(seconds));
    }

    private String capturedOutput() {
        return output.toString().trim();
    }

    @Test
    void shouldPrintMessage() {
        execute("Alice -> I love the weather today");
        advanceBy(5 * 60);

        output.reset();
        execute("Alice");

        assertEquals("I love the weather today (5 minutes ago)", capturedOutput());
    }

    @Test
    void shouldShowPersonMessages() {
        execute("Bob -> Damn! We lost!");
        advanceBy(60);
        execute("Bob -> Good game though.");
        advanceBy(60);

        output.reset();
        execute("Bob");

        String[] lines = capturedOutput().split(System.lineSeparator());
        assertEquals(2, lines.length);
        assertEquals("Good game though. (1 minute ago)", lines[0]);
        assertEquals("Damn! We lost! (2 minutes ago)", lines[1]);
    }

    @Test
    void shouldShowFollowingMessages() {
        execute("Alice -> I love the weather today");
        advanceBy(3 * 60);
        execute("Charlie -> I'm in New York today! Anyone want to have a coffee?");
        advanceBy(2 * 60);

        execute("Charlie follows Alice");

        output.reset();
        execute("Charlie wall");

        String[] lines = capturedOutput().split(System.lineSeparator());
        assertEquals(2, lines.length);
        assertEquals("Charlie - I'm in New York today! Anyone want to have a coffee? (2 minutes ago)", lines[0]);
        assertEquals("Alice - I love the weather today (5 minutes ago)", lines[1]);
    }

    @Test
    void shouldShowMessagesInOrder() {
        execute("Alice -> I love the weather today");
        advanceBy(2 * 60);
        execute("Bob -> Damn! We lost!");
        advanceBy(60);
        execute("Bob -> Good game though.");
        advanceBy(2);
        execute("Charlie -> I'm in New York today! Anyone want to have a coffee?"); // T+3min+2s

        execute("Charlie follows Alice");
        execute("Charlie follows Bob");

        advanceBy(13);

        output.reset();
        execute("Charlie wall");

        String[] lines = capturedOutput().split(System.lineSeparator());
        assertEquals(4, lines.length);
        assertEquals("Charlie - I'm in New York today! Anyone want to have a coffee? (13 seconds ago)", lines[0]);
        assertEquals("Bob - Good game though. (15 seconds ago)", lines[1]);
        assertEquals("Bob - Damn! We lost! (1 minute ago)", lines[2]);
        assertEquals("Alice - I love the weather today (3 minutes ago)", lines[3]);
    }
}
