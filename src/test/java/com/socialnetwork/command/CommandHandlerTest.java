package com.socialnetwork.command;

import com.socialnetwork.service.SocialNetworkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandHandlerTest {

    @Mock
    private SocialNetworkService service;
    private ByteArrayOutputStream outputStream;
    private CommandHandler handler;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        handler = new CommandHandler(service, new PrintStream(outputStream));
    }

    @Test
    void shouldPostMessage() {
        handler.handle(new Command.Post("Alice", "Hello"));

        verify(service).post("Alice", "Hello");
    }

    @Test
    void shouldPrintsResults() {
        when(service.read("Alice")).thenReturn(List.of("Hello (1 minute ago)"));

        handler.handle(new Command.Read("Alice"));

        assertTrue(outputStream.toString().contains("Hello (1 minute ago)"));
    }

    @Test
    void shouldFollowPerson() {
        handler.handle(new Command.Follow("Charlie", "Alice"));

        verify(service).follow("Charlie", "Alice");
    }

    @Test
    void shouldPrintWall() {
        when(service.wall("Charlie")).thenReturn(List.of("Charlie - Hi (1 second ago)"));

        handler.handle(new Command.Wall("Charlie"));

        assertTrue(outputStream.toString().contains("Charlie - Hi (1 second ago)"));
    }

    @Test
    void shouldPrintNothingWhenEmptyWall() {
        when(service.read("Nobody")).thenReturn(List.of());

        handler.handle(new Command.Read("Nobody"));

        verify(service).read("Nobody");
        assertTrue(outputStream.toString().isBlank());
    }
}
