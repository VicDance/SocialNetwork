# Social Network – Console Application

A console-based social networking application built in Java 21,
following clean architecture, TDD principles, and SOLID design.

---

## How to Run

```bash
# Build & run all tests
mvn test

# Build the executable JAR
mvn package

# Run the application (interactive mode)
java -jar target/social-network-1.0.0.jar

# Run with piped input
echo "Alice -> I love the weather today
Alice" | java -jar target/social-network-1.0.0.jar
```

---

## Supported Commands

| Command                        | Example                          |
|--------------------------------|----------------------------------|
| `<user> -> <message>`          | `Alice -> I love the weather`    |
| `<user>`                       | `Alice`                          |
| `<user> follows <user>`        | `Charlie follows Alice`          |
| `<user> wall`                  | `Charlie wall`                   |

---

## Architecture

```
Console I/O
    │
    ▼
CommandParser          Parses raw strings → typed Command objects
    │
    ▼
CommandHandler         Dispatches commands, writes output
    │
    ▼
SocialNetworkService   Core business logic (post / read / follow / wall)
    │              │
    ▼              ▼
MessageRepository  FollowRepository   Storage abstractions
    │              │
    ▼              ▼
InMemory*          In-memory implementations (swappable)
```

### Layer responsibilities

| Layer | Responsibility |
|---|---|
| `CommandParser` | Translate raw strings into `Command` value objects |
| `CommandHandler` | Route commands to service, write results to `PrintStream` |
| `SocialNetworkService` | All business rules; no I/O |
| `Repository interfaces` | Storage contract, decoupled from implementation |
| `InMemory*` | Concrete in-memory stores; replaceable with DB implementations |

---

## Key Design Decisions

### 1. Sealed interface + records for Commands (Java 21)
```java
public sealed interface Command permits Command.Post, Command.Read, Command.Follow, Command.Wall {
    record Post(String author, String content) implements Command {}
    ...
}
```
The compiler enforces exhaustive handling in `switch` expressions.
Adding a new command type causes a compile error at every unhandled switch — impossible to forget.

### 2. Clock abstraction for testability
```java
@FunctionalInterface
public interface Clock {
    Instant now();
    static Clock system() { return Instant::now; }
}
```
Instead of calling `Instant.now()` directly, all services accept a `Clock`.
Tests inject a mutable fixed clock, making time-sensitive assertions fully deterministic.

### 3. Service layer has zero I/O
`SocialNetworkService` only works with domain objects and returns `List<String>`.
It never reads from stdin or writes to stdout — making it independently testable
and trivially adaptable to REST, GUI, or any other front-end.

### 4. Repository pattern with interfaces
`MessageRepository` and `FollowRepository` are interfaces.
`InMemoryMessageRepository` and `InMemoryFollowRepository` are the current implementations.
Swapping to JPA/SQL requires zero changes to the service layer.

### 5. Pattern-matching switch dispatch (Java 21)
```java
switch (command) {
    case Command.Post(var author, var content)        -> service.post(author, content);
    case Command.Read(var author)                     -> printLines(service.read(author));
    case Command.Follow(var follower, var followee)   -> service.follow(follower, followee);
    case Command.Wall(var user)                       -> printLines(service.wall(user));
}
```
No instanceof chains, no casting, compiler-checked exhaustiveness.

---

## Test Strategy

Tests are written with JUnit 5 and Mockito, covering all layers independently.

| Test class | Layer | What it verifies |
|---|---|---|
| `CommandParserTest` | Parser | All 4 command types, edge cases (null, blank) |
| `TimeFormatterTest` | Formatter | All time ranges (seconds → days), singular/plural |
| `SocialNetworkServiceTest` | Service | Post, read, follow, wall logic with controlled clock |
| `CommandHandlerTest` | Handler | Delegation to service, output formatting (Mockito) |
| `SocialNetworkIntegrationTest` | Full stack | Exact spec scenarios end-to-end |

### TDD Approach (iterative steps)
1. `Message` record + `Clock` abstraction
2. `MessageRepository` interface + in-memory implementation
3. `SocialNetworkService.post()` + `read()` with tests
4. `CommandParser` with tests for each command type
5. `FollowRepository` + `SocialNetworkService.follow()` + `wall()` with tests
6. `CommandHandler` with Mockito tests
7. Integration tests matching spec scenarios exactly

---

## Extensibility Notes

- **New command type**: add a `record` to `Command`, handle in `CommandHandler` switch — compiler guides you
- **Persistence**: implement `MessageRepository` / `FollowRepository` for your DB of choice
- **Mentions / hashtags**: add parsing logic to `CommandParser`, new service methods
- **REST API**: reuse `SocialNetworkService` as-is, add a controller layer
- **Pagination**: `findByAuthor` signature can accept `Pageable` without touching the service
