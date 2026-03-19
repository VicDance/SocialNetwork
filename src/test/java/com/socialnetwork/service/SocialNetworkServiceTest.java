package com.socialnetwork.service;

import com.socialnetwork.repository.InMemoryFollowRepository;
import com.socialnetwork.repository.InMemoryMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SocialNetworkServiceTest {

    private static final Instant TIME = Instant.parse("2024-01-01T12:00:00Z");

    private AtomicReference<Instant> clockRef;
    private SocialNetworkService service;

    @BeforeEach
    void setUp() {
        clockRef = new AtomicReference<>(TIME);
        var clock = (com.socialnetwork.model.Clock) clockRef::get;
        var messageRepo = new InMemoryMessageRepository();
        var followRepo = new InMemoryFollowRepository();
        var formatter = new TimeFormatter(clock);
        service = new SocialNetworkService(messageRepo, followRepo, formatter, clock);
    }

    private void advanceBy(long seconds) {
        clockRef.updateAndGet(t -> t.plusSeconds(seconds));
    }

    @Nested
    class Posting {

        @Test
        void shouldPostMessageInTimeline() {
            service.post("Alice", "I love the weather today");

            List<String> timeline = service.read("Alice");

            assertEquals(1, timeline.size());
            assertTrue(timeline.getFirst().contains("I love the weather today"));
        }

        @Test
        void shouldPostMessagesInOrder() {
            service.post("Bob", "Damn! We lost!");
            advanceBy(60);
            service.post("Bob", "Good game though.");

            List<String> timeline = service.read("Bob");

            assertEquals(2, timeline.size());
            assertTrue(timeline.get(0).contains("Good game though."));
            assertTrue(timeline.get(1).contains("Damn! We lost!"));
        }

        @Test
        void shouldReturnEmptyWhenUnknownUser() {
            assertTrue(service.read("Nobody").isEmpty());
        }
    }

    @Nested
    class Reading {

        @Test
        void shouldFormatTime() {
            service.post("Alice", "I love the weather today");
            advanceBy(5 * 60);

            List<String> timeline = service.read("Alice");

            assertEquals("I love the weather today (5 minutes ago)", timeline.getFirst());
        }
    }

    @Nested
    class Following {

        @Test
        void shouldShowFollowingAndOwnPosts() {
            service.post("Alice", "I love the weather today");
            advanceBy(2 * 60);
            service.post("Bob", "Damn! We lost!");
            advanceBy(60);
            service.post("Bob", "Good game though.");
            advanceBy(2);
            service.post("Charlie", "I'm in New York today! Anyone want to have a coffee?");

            service.follow("Charlie", "Alice");
            service.follow("Charlie", "Bob");

            List<String> wall = service.wall("Charlie");

            assertEquals(4, wall.size());
            assertTrue(wall.get(0).startsWith("Charlie -"));
            assertTrue(wall.get(1).startsWith("Bob -") && wall.get(1).contains("Good game though."));
            assertTrue(wall.get(2).startsWith("Bob -") && wall.get(2).contains("Damn! We lost!"));
            assertTrue(wall.get(3).startsWith("Alice -"));
        }

        @Test
        void shouldShowWallInCorrectOrder() {
            service.post("Alice", "Hello");
            advanceBy(60);
            service.follow("Charlie", "Alice");

            List<String> wall = service.wall("Charlie");

            assertEquals("Alice - Hello (1 minute ago)", wall.getFirst());
        }

        @Test
        void shouldReturnEmptyWhenNoPostsAndNoFollows() {
            assertTrue(service.wall("Nobody").isEmpty());
        }

        @Test
        void shouldShowFollowingUsers() {
            service.post("Alice", "Post before follow");
            service.follow("Charlie", "Alice");
            service.post("Alice", "Post after follow");

            List<String> wall = service.wall("Charlie");
            assertEquals(2, wall.size());
        }
    }
}
