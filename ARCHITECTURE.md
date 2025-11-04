# Architecture Documentation

## System Overview

API-Futbol is a RESTful web service built using Spring Boot that provides football statistics, match information, and predictions. The system is designed with a layered architecture focusing on maintainability, testability, and security.

## Architecture Decisions

### 1. Technology Stack

- **Spring Boot 3.5.5**: Chosen for its robust ecosystem, excellent documentation, and modern features
- **Java 17**: Selected for improved performance, modern language features, and long-term support
- **Spring Security**: Implements authentication and authorization
- **Spring Data JPA**: Simplifies data access layer
- **HSQLDB/H2**: In-memory databases for development and testing
- **Swagger/OpenAPI**: API documentation and testing interface
- **JUnit & MockMvc**: Comprehensive testing framework
- **JaCoCo**: Code coverage metrics
- **Maven**: Build automation and dependency management

### 2. Security Design

The application implements a dual authentication system:

#### JWT Authentication
- Used for interactive sessions
- Stateless authentication
- Token expiration and refresh mechanisms
- Secure password storage with BCrypt

#### API Key Authentication
- Used for programmatic access
- Permanent until revoked
- Stored hashed in database
- Rate limiting support

The security flow:
1. Request arrives at `ApiKeyAuthenticationFilter` or `JwtAuthenticationFilter`
2. Filter extracts credentials
3. Authentication provider validates credentials
4. Security context is populated
5. Request proceeds to controller

### 3. Data Model

#### Core Entities

```
User
├── id: Long
├── username: String
├── password: String (hashed)
└── apiKeys: List<ApiKey>

ApiKey
├── id: Long
├── name: String
├── keyHash: String
├── fingerprint: String
└── user: User

Team
├── id: Long
├── name: String
└── players: List<Player>

Player
├── id: Long
├── name: String
├── team: Team
└── stats: List<PlayerStats>

Match
├── id: Long
├── homeTeam: Team
├── awayTeam: Team
├── matchDate: LocalDateTime
└── status: MatchStatus

PlayerStats
├── id: Long
├── player: Player
├── match: Match
├── goals: Integer
├── assists: Integer
└── rating: Double
```

### 4. Package Structure

The application follows a layered architecture:

```
com.dapp.api_futbol/
├── config/         # Configuration classes
│   ├── SecurityConfig         # Security setup
│   ├── OpenApiConfig         # Swagger configuration
│   └── DataLoader            # Development data seeding
│
├── controller/    # REST endpoints
│   ├── AuthController        # Authentication endpoints
│   ├── TeamsPlayersController # Team & player data
│   └── FootballDataController # Match & statistics
│
├── dto/          # Data Transfer Objects
│   ├── MatchDTO
│   ├── PlayerDTO
│   └── TeamDTO
│
├── model/        # Domain entities
│   ├── User
│   ├── ApiKey
│   ├── Team
│   └── Player
│
├── repository/   # Data access layer
│   ├── UserRepository
│   ├── TeamRepository
│   └── MatchRepository
│
├── security/     # Security components
│   ├── JwtTokenProvider
│   └── ApiKeyAuthenticationFilter
│
└── service/      # Business logic
    ├── UserService
    ├── ApiKeyService
    └── FootballDataService
```

### 5. Request Flow

Example flow for retrieving player performance:

```
HTTP Request
    ↓
ApiKeyAuthenticationFilter/JwtAuthenticationFilter
    ↓
SecurityContext populated
    ↓
PlayerPerformanceController
    ↓
PlayerPerformanceService
    ↓
PlayerStatsRepository & PlayerRepository
    ↓
Database
    ↓
DTO mapping
    ↓
HTTP Response
```

### 6. Testing Strategy

The project implements a comprehensive testing strategy:

1. **Unit Tests**
   - Test individual components in isolation
   - Use Mockito for dependencies
   - Focus on business logic

2. **Integration Tests**
   - Test component interactions
   - Use in-memory database
   - Test security configuration

3. **End-to-End Tests**
   - Test complete flows
   - Use seeded test data
   - Validate API contracts

### 7. Development Environment

Development environment is configured for rapid iteration:
- In-memory database with seeded data
- Auto-reload with Spring DevTools
- Swagger UI for API testing
- H2 Console for database inspection

### 8. Monitoring and Observability

The application includes:
- Spring Boot Actuator endpoints
- Custom health indicators
- Performance metrics
- API usage statistics

### 9. Future Considerations

Planned improvements:
- Caching layer for frequently accessed data
- Rate limiting implementation
- Advanced prediction models
- WebSocket support for live updates
- Distributed tracing
- Event sourcing for query history