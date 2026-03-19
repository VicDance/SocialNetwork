# Social Network

A console-based social networking application built in Java 21.

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

## Test Strategy

Tests are written with JUnit 5 and Mockito, covering all layers independently.

| Test class | Layer | What it verifies |
|---|---|---|
| `CommandParserTest` | Parser | All 4 command types, edge cases (null, blank) |
| `TimeFormatterTest` | Formatter | All time ranges (seconds → days), singular/plural |
| `SocialNetworkServiceTest` | Service | Post, read, follow, wall logic with controlled clock |
| `CommandHandlerTest` | Handler | Delegation to service, output formatting (Mockito) |
| `SocialNetworkIntegrationTest` | Full stack | Exact spec scenarios end-to-end |

## Extensibility Notes

- **Persistence**: implement `MessageRepository` / `FollowRepository` for your DB
- **Mentions / hashtags**: add parsing logic to `CommandParser`, new service methods
- **REST API**: reuse `SocialNetworkService` as-is, add a controller layer
- **Pagination**: `findByAuthor` signature can accept `Pageable` without touching the service
