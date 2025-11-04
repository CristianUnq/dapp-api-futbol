# Football API Service (API-Futbol)

REST API service for football (soccer) statistics, match information, and predictions. Built with Spring Boot 3.5.5 and Java 17.

## Features

- **Authentication**: Supports both JWT tokens and API Keys
- **Match Information**: Upcoming matches and historical data
- **Player Statistics**: Performance metrics and historical stats
- **Team Data**: Team information and roster details
- **Query History**: Track and retrieve query history
- **Match Predictions**: Basic prediction model for match outcomes

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/CristianUnq/dapp-api-futbol.git
   cd dapp-api-futbol
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application (development mode):
   ```bash
   ./mvnw spring-boot:run -Dspring.profiles.active=dev
   ```

The API will be available at `http://localhost:8080`

### Configuration

The application uses Spring profiles for different environments:

- `dev`: Development environment (in-memory DB, sample data)
- `prod`: Production environment 
- `test`: Testing environment (in-memory DB, test data)

Configuration files are in `src/main/resources/`:
- `application.properties`: Base configuration
- `application-dev.properties`: Development settings
- `application-prod.properties`: Production settings

## API Documentation

### Authentication

#### Register a new user
```http
POST /auth/register
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```

#### Generate API Key
```http
POST /auth/apikeys
Authorization: Bearer {jwt-token}
Content-Type: application/json

{
  "name": "string"
}
```

### Matches

#### Get Upcoming Matches
```http
GET /matches/upcoming?team={teamName}
Authorization: Bearer {jwt-token} or ApiKey {api-key}
```

### Player Performance

#### Get Player Stats
```http
GET /players/performance/{playerId}
Authorization: Bearer {jwt-token} or ApiKey {api-key}
```

### Query History

#### Get Query History
```http
GET /history?page=0&size=10
Authorization: Bearer {jwt-token} or ApiKey {api-key}
```

## Security

The API implements two authentication mechanisms:
1. JWT Tokens: For interactive sessions
2. API Keys: For programmatic access

Protected endpoints require either:
- A valid JWT token in the Authorization header (`Bearer {token}`)
- A valid API key in the Authorization header (`ApiKey {key}`)

## Testing

Run tests with:
```bash
./mvnw test
```

For test coverage report:
```bash
./mvnw verify
```

Coverage reports are generated in `target/site/jacoco/index.html`

## Development

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── dapp/
│   │           └── api_futbol/
│   │               ├── config/         # Configuration classes
│   │               ├── controller/     # REST endpoints
│   │               ├── dto/           # Data Transfer Objects
│   │               ├── model/         # Domain models
│   │               ├── repository/    # Data access layer
│   │               ├── security/      # Security configuration
│   │               └── service/       # Business logic
│   └── resources/                    # Application properties
└── test/
    └── java/                        # Test classes
```

### Key Components

- `SecurityConfig`: Security configuration and authentication
- `DataLoader`: Loads sample data in dev/test environments
- `OpenApiConfig`: Swagger/OpenAPI configuration
- `JwtTokenProvider`: JWT token generation and validation
- `ApiKeyAuthenticationFilter`: API key authentication

### Documentation

- Swagger UI: http://localhost:8080/swagger-ui.html (dev profile)
- API Docs: http://localhost:8080/v3/api-docs

## Contributing

1. Create a feature branch
2. Commit your changes
3. Push to your branch
4. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.