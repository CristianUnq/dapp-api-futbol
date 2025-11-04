# Entrega 2 Overview

## 1. Core Requirements

### 1.1 API Documentation (OpenAPI/Swagger)
- **Status**: Implemented
- **Key Components**:
  ```java
  // OpenApiConfig.java
  @Configuration
  @Profile({"dev", "test"})
  public class OpenApiConfig {
      // Configures Swagger UI and API documentation
      // Available at /swagger-ui.html in dev profile
  }
  ```
- **Access**: http://localhost:8080/swagger-ui.html

### 1.2 Upcoming Matches Endpoint
- **Status**: Implemented
- **Endpoint**: `GET /matches/upcoming?team={teamName}`
- **Key Components**:
  ```java
  // FootballDataController.java
  @GetMapping("/matches/upcoming")
  public ResponseEntity<List<MatchDTO>> getUpcomingMatches(
      @RequestParam String team) {
      // Returns upcoming matches for specified team
  }
  ```
- **Authentication**: Requires JWT token or API key
- **Response**: List of upcoming matches with dates and teams

### 1.3 Query History
- **Status**: Implemented
- **Endpoint**: `GET /history?page=0&size=10`
- **Features**:
  - Tracks all API queries
  - Pagination support
  - Includes query parameters and timestamps
  - User-specific history

### 1.4 Player Performance Statistics
- **Status**: Implemented
- **Endpoint**: `GET /players/performance/{playerId}`
- **Key Features**:
  - Last 10 matches statistics
  - Average rating calculation
  - Goals and assists tracking
  ```java
  // PlayerPerformanceController.java
  @GetMapping("/players/performance/{playerId}")
  public ResponseEntity<PlayerPerformanceDTO> getPlayerPerformance(
      @PathVariable Long playerId) {
      // Returns detailed player statistics
  }
  ```

### 1.5 Match Prediction
- **Status**: In Progress
- **Endpoint**: `GET /matches/{matchId}/prediction`
- **Implementation Plan**:
  - Basic heuristic model
  - Based on team and player statistics
  - Historical performance analysis

## 2. Security Implementation

### 2.1 JWT Authentication
```java
// AuthController.java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    // Validates credentials and returns JWT token
}
```

### 2.2 API Key Authentication
```java
// ApiKeyAuthenticationFilter.java
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    // Validates API keys for protected endpoints
}
```

## 3. Data Models

### 3.1 Match DTO
```java
public class MatchDTO {
    private Long id;
    private String homeTeam;
    private String awayTeam;
    private LocalDateTime matchDate;
    private String competition;
    // Additional match details
}
```

### 3.2 Player Performance DTO
```java
public class PlayerPerformanceDTO {
    private Long playerId;
    private String playerName;
    private Double averageRating;
    private Integer totalGoals;
    private Integer totalAssists;
    private List<MatchPerformance> lastMatches;
}
```

## 4. Testing Coverage

### 4.1 Controller Tests
```java
// PlayerPerformanceControllerTest.java
@Test
void getPlayerPerformance_WithValidId_ReturnsStats() {
    // Tests player performance endpoint
}

// FootballDataControllerTest.java
@Test
void getUpcomingMatches_WithValidTeam_ReturnsMatches() {
    // Tests upcoming matches endpoint
}
```

### 4.2 Service Tests
- Unit tests for business logic
- Integration tests for data flow
- Security tests for authentication

## 5. Database Schema

### 5.1 Core Tables
```sql
-- Matches table
CREATE TABLE matches (
    id BIGINT PRIMARY KEY,
    home_team_id BIGINT,
    away_team_id BIGINT,
    match_date TIMESTAMP,
    competition VARCHAR(255)
);

-- Player Stats table
CREATE TABLE player_stats (
    id BIGINT PRIMARY KEY,
    player_id BIGINT,
    match_id BIGINT,
    goals INT,
    assists INT,
    rating DOUBLE
);
```

## 6. Key Features

### 6.1 Authentication Flow
1. User obtains authentication (JWT or API key)
2. Includes token in request header
3. System validates credentials
4. Returns requested data

### 6.2 Data Flow
1. Request received and authenticated
2. Data retrieved from database
3. Transformed into DTOs
4. Query logged in history
5. Response returned to client

## 7. Testing Instructions

### 7.1 Running Tests
```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw verify
```

### 7.2 Manual Testing via Swagger
1. Access Swagger UI
2. Authenticate with JWT or API key
3. Try endpoints with sample data
4. Verify responses

## 8. Remaining Tasks

1. ✅ OpenAPI documentation
2. ✅ Upcoming matches endpoint
3. ✅ Query history tracking
4. ✅ Player performance statistics
5. ⏳ Match prediction implementation
6. ⏳ Coverage job in CI

## 9. API Usage Examples

### 9.1 Get Upcoming Matches
```http
GET /matches/upcoming?team=Barcelona
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Response:
{
    "matches": [
        {
            "id": 1,
            "homeTeam": "Barcelona",
            "awayTeam": "Real Madrid",
            "matchDate": "2025-12-01T20:00:00",
            "competition": "La Liga"
        }
        // More matches...
    ]
}
```

### 9.2 Get Player Performance
```http
GET /players/performance/123
Authorization: ApiKey abcd1234...

Response:
{
    "playerId": 123,
    "playerName": "John Doe",
    "averageRating": 8.5,
    "totalGoals": 15,
    "totalAssists": 10,
    "lastMatches": [
        {
            "matchId": 1,
            "date": "2025-10-15",
            "goals": 2,
            "assists": 1,
            "rating": 9.0
        }
        // More matches...
    ]
}
```

## 10. Presentation Tips

### 10.1 Demo Flow
1. Show Swagger documentation
2. Demonstrate authentication
3. Show upcoming matches endpoint
4. Display player statistics
5. Explain query history

### 10.2 Key Points to Emphasize
- Comprehensive API documentation
- Robust security implementation
- Detailed player statistics
- Query history tracking
- Test coverage and quality assurance