# API-Futbol: System Overview Presentation

## 1. Introduction

API-Futbol is a RESTful service that provides football statistics, match information, and predictions. It's built with Spring Boot 3.5.5 and Java 17, featuring secure authentication and comprehensive data management.

## 2. Core Components Overview

### Main Application Class
`ApiFutbolApplication.java`
- Entry point of the application
- Enables Spring Boot auto-configuration
- Activates component scanning

### Configuration Package (`config/`)

1. **SecurityConfig.java**
   - Configures authentication and authorization
   - Sets up JWT and API Key filters
   - Defines security rules for endpoints
   - Returns 401 for unauthenticated requests

2. **OpenApiConfig.java**
   - Configures Swagger/OpenAPI documentation
   - Defines API information and security schemes
   - Available in dev profile at /swagger-ui.html

3. **DataLoader.java**
   - Initializes sample data for development
   - Creates teams, players, and matches
   - Only runs in dev/test profiles

4. **SchedulerConfig.java**
   - Configures scheduled tasks
   - Updates match data periodically

### Controllers Package (`controller/`)

1. **AuthController.java**
   - Handles user registration
   - Manages login and JWT token generation
   - Manages API key creation/revocation
   ```http
   POST /auth/register
   POST /auth/login
   POST /auth/apikeys
   ```

2. **FootballDataController.java**
   - Provides match information
   - Handles upcoming matches queries
   ```http
   GET /matches/upcoming?team={name}
   ```

3. **TeamsPlayersController.java**
   - Manages team and player data
   - Provides player statistics
   ```http
   GET /teams/{id}
   GET /players/{id}
   ```

4. **PlayerPerformanceController.java**
   - Calculates player performance metrics
   - Returns last 10 matches statistics
   ```http
   GET /players/performance/{id}
   ```

### Models Package (`model/`)

1. **User.java & ApiKey.java**
   - User authentication and API key management
   - Secure password/key storage

2. **Team.java & Player.java**
   - Core domain entities
   - Team roster management
   - Player information

3. **Match.java & PlayerStats.java**
   - Match details and scheduling
   - Player performance tracking

### Services Package (`service/`)

1. **FootballDataService.java**
   - Business logic for match data
   - Team and player statistics

2. **ApiKeyService.java**
   - API key generation and validation
   - Key management operations

3. **PlayerPerformanceService.java**
   - Calculates performance metrics
   - Aggregates match statistics

### Security Package (`security/`)

1. **JwtTokenProvider.java**
   - Generates and validates JWT tokens
   - Manages token expiration

2. **ApiKeyAuthentication.java**
   - Validates API keys
   - Manages API key permissions

3. **Security Filters**
   - `JwtAuthenticationFilter.java`
   - `ApiKeyAuthenticationFilter.java`
   - Handle different authentication methods

### Repositories Package (`repository/`)

Database access interfaces for:
- Users and API keys
- Teams and players
- Matches and statistics
- Query history

## 3. Key Features Demonstration

### Authentication Flow
1. User registers or logs in
2. Receives JWT token or creates API key
3. Uses token/key for subsequent requests

### Match Information Flow
1. Client requests upcoming matches
2. System validates authentication
3. Retrieves and returns match data
4. Logs query in history

### Player Statistics Flow
1. Request player performance
2. System calculates metrics
3. Returns aggregated statistics

## 4. Database Structure

Uses HSQLDB (in-memory for dev/test):
- User management tables
- Team and player data
- Match information
- Performance statistics

## 5. Testing Strategy

1. **Unit Tests**
   - Controller tests with MockMvc
   - Service layer tests with Mockito
   - Repository tests with H2

2. **Integration Tests**
   - End-to-end API testing
   - Security validation
   - Data consistency checks

## 6. Development Environment

### Profiles
- `dev`: Development with sample data
- `test`: Testing environment
- `prod`: Production configuration

### Tools
- Maven for build management
- JaCoCo for code coverage
- Swagger UI for API testing

## 7. Future Enhancements

1. Match Prediction Engine
2. Real-time Updates
3. Advanced Statistics
4. Performance Optimization

## Key Points for Presentation

1. **Security First**
   - Dual authentication system
   - Secure password/key storage
   - Request validation

2. **Clean Architecture**
   - Layered design
   - Separation of concerns
   - Maintainable codebase

3. **Scalability**
   - Modular components
   - Independent services
   - Easy to extend

4. **Quality Assurance**
   - Comprehensive testing
   - Code coverage
   - Documentation

## Demonstration Script

1. Show API documentation (Swagger UI)
2. Demo user registration and authentication
3. Retrieve match information
4. Show player statistics
5. Demonstrate security features

## Common Questions

1. **Why two authentication methods?**
   - JWT for interactive sessions
   - API keys for programmatic access

2. **How is data kept current?**
   - Scheduled updates
   - Real-time integration capability

3. **How scalable is the system?**
   - Stateless design
   - Caching-ready architecture
   - Modular components

4. **What makes it secure?**
   - Token-based authentication
   - Encrypted storage
   - Request validation
   - Rate limiting capability