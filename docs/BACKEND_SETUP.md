# DevSwipe Backend Setup Guide

## 🚀 Overview

This guide provides detailed instructions for setting up and running the Spring Boot backend for DevSwipe. The backend is built with Kotlin, Spring Boot, and PostgreSQL for data persistence.

## 📋 Prerequisites

Before setting up the backend, ensure you have:

| Software | Version | Download Link |
|----------|---------|---------------|
| **Java Development Kit (JDK)** | JDK 17 or higher | [Download](https://adoptium.net/) |
| **Gradle** | 8.0+ (or use wrapper included with project) | [Download](https://gradle.org/install/) |
| **Git** | Latest | [Download](https://git-scm.com/) |
| **PostgreSQL** | 14+ | [Download](https://www.postgresql.org/download/) |

## 🚀 Quick Setup

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/DevSwipe.git
cd DevSwipe
```

### 2. Navigate to Backend Directory
```bash
cd backend
```

### 3. Install and Configure PostgreSQL
1. Install PostgreSQL from [official website](https://www.postgresql.org/download/)
2. Start the PostgreSQL service
3. Create a database and user for DevSwipe:
   ```sql
   CREATE DATABASE devswipe_db;
   CREATE USER devswipe_user WITH PASSWORD 'devswipe_password';
   GRANT ALL PRIVILEGES ON DATABASE devswipe_db TO devswipe_user;
   ```

### 4. Configure Database Connection
Update the database configuration in `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/devswipe_db
spring.datasource.username=devswipe_user
spring.datasource.password=devswipe_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration
server.port=8080

# JWT Configuration
jwt.secret=your-super-secret-jwt-key-change-in-production
jwt.expiration=86400000  # 24 hours in milliseconds

# Logging
logging.level.org.springframework.security=DEBUG
```

### 5. Run the Backend
```bash
# Using Gradle wrapper (recommended)
./gradlew bootRun

# Or if you have Gradle installed globally
gradle bootRun
```

The backend will start on `http://localhost:8080`

## 🔧 Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/first/devswipe/
│   │   │       ├── DevSwipeBackendApplication.kt
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.kt
│   │   │       │   └── JwtConfig.kt
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.kt
│   │   │       │   ├── ProjectController.kt
│   │   │       │   ├── UserProfileController.kt
│   │   │       │   ├── CollabPostController.kt
│   │   │       │   └── FileUploadController.kt
│   │   │       ├── dto/
│   │   │       │   ├── AuthDtos.kt
│   │   │       │   ├── ProjectDtos.kt
│   │   │       │   └── ProfileDtos.kt
│   │   │       ├── entity/
│   │   │       │   ├── User.kt
│   │   │       │   ├── Project.kt
│   │   │       │   ├── UserProfile.kt
│   │   │       │   └── CollabPost.kt
│   │   │       ├── repository/
│   │   │       │   ├── UserRepository.kt
│   │   │       │   ├── ProjectRepository.kt
│   │   │       │   ├── UserProfileRepository.kt
│   │   │       │   └── CollabPostRepository.kt
│   │   │       ├── security/
│   │   │       │   ├── JwtTokenProvider.kt
│   │   │       │   └── JwtAuthenticationFilter.kt
│   │   │       └── service/
│   │   │           ├── AuthService.kt
│   │   │           ├── ProjectService.kt
│   │   │           ├── UserProfileService.kt
│   │   │           └── FileUploadService.kt
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-dev.properties
│   └── test/
│       └── kotlin/
│           └── com/first/devswipe/
└── build.gradle.kts
```

## 🏗️ Key Components

### 1. Security Configuration
The backend uses Spring Security with JWT authentication:
- Passwords are securely hashed using BCrypt
- JWT tokens for session management
- Role-based access control (currently USER role)

### 2. Database Entities
- **User**: Core user authentication and basic profile data
- **UserProfile**: Extended profile information (skills, interests, onboarding status)
- **Project**: Project ideas with title, description, tags, and difficulty
- **CollabPost**: Posts for finding collaborators on projects

### 3. API Endpoints
All endpoints are prefixed with `/api/`:

#### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/me` - Get current user profile

#### Projects
- `GET /api/projects` - Get all projects
- `GET /api/projects/my-projects` - Get projects created by current user
- `POST /api/projects` - Create a new project
- `PUT /api/projects/{id}` - Update a project
- `DELETE /api/projects/{id}` - Delete a project
- `GET /api/projects/filter?difficulty=...&tags=...` - Filter projects

#### User Profiles
- `GET /api/users/{id}` - Get user profile
- `PUT /api/users/{id}` - Update user profile
- `PATCH /api/users/{id}/skills` - Update user skills
- `PATCH /api/users/{id}/interests` - Update user interests
- `PATCH /api/users/{id}/onboarding` - Complete onboarding

#### Collaborator Posts
- `GET /api/collaborations` - Get all collab posts
- `GET /api/collaborations/my-collaborations` - Get user's collab posts
- `POST /api/collaborations` - Create a collab post
- `PUT /api/collaborations/{id}` - Update a collab post
- `DELETE /api/collaborations/{id}` - Delete a collab post

### 4. File Upload
- `POST /api/upload` - Upload files (profile pictures, project images)
- Uses multipart form data
- Files are stored in the `uploads/` directory

## 🛠️ Development Commands

### Build the Project
```bash
# Build the backend
./gradlew build

# Build without tests
./gradlew build -x test

# Create executable JAR
./gradlew bootJar
```

### Run Tests
```bash
# Run all tests
./gradlew test

# Run tests with continuous feedback (watch mode)
./gradlew test --continuous
```

### Development Mode
```bash
# Run with hot reload (for development)
./gradlew bootRun --continuous

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## 🔐 Security Configuration

### JWT Token Management
- Tokens expire after 24 hours (configurable in `application.properties`)
- Refresh tokens are not implemented in the current version
- All secured endpoints require a valid JWT token in the `Authorization` header as `Bearer {token}`

### Password Security
- Passwords are hashed using BCrypt with strength 12
- Minimum password length is 8 characters
- Password validation includes checks for special characters and mixed case

## 🗄️ Database Migrations

The backend uses Flyway for database migrations:
- Migration files are located in `src/main/resources/db/migration/`
- Migration files follow the naming convention: `V{number}__{description}.sql`
- Database schema is automatically updated when the application starts

## 🧪 Testing

### Unit Tests
- Located in `src/test/kotlin/`
- Use JUnit 5 for testing
- MockK for mocking dependencies
- Spring Boot Test for integration testing

### Running Tests
```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# Run a specific test class
./gradlew :test --tests "com.first.devswipe.ProjectControllerTest"
```

## 🚀 Deployment

### Building for Production
```bash
# Build an executable JAR for production
./gradlew bootJar

# The JAR file will be in build/libs/
```

### Environment Variables for Production
For production deployment, it's recommended to use environment variables instead of `application.properties`:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://production-db:5432/devswipe_db
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password
JWT_SECRET=your-production-jwt-secret-key
SERVER_PORT=8080
```

### Docker Support
```dockerfile
FROM openjdk:17-jdk-slim

COPY build/libs/devswipe-backend-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🔍 API Documentation

### Authentication

#### Register
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe"
}
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Jane",
    "university": null,
    "onboardingCompleted": false
  },
  "message": "Login successful"
}
```

## 🛠️ Troubleshooting

### Common Issues

#### Database Connection Issues
**Error**: `org.postgresql.util.PSQLException: FATAL: database "devswipe_db" does not exist`
```
Solution: Create the database in PostgreSQL:
CREATE DATABASE devswipe_db;
```

#### Port Already in Use
**Error**: `java.net.BindException: Address already in use`
```
Solution: Change the port in application.properties:
server.port=8081
```

#### Gradle Build Issues
**Error**: `Could not find or load main class org.gradle.wrapper.GradleWrapperMain`
```
Solution: Ensure gradlew and gradlew.bat files exist in the backend directory
On Windows: gradlew.bat bootRun
On Unix: ./gradlew bootRun
```

### Debugging Tips
- Enable debug logging by adding `logging.level.org.springframework=DEBUG` to `application.properties`
- Check the console output for detailed error messages
- Verify database connection parameters are correct
- Ensure PostgreSQL service is running

## 📊 Monitoring and Logging

### Default Logging
- Logs are output to console by default
- Log level can be configured in `application.properties`
- Structured logging is used for important events

### Health Checks
- Health endpoint: `GET /actuator/health`
- Requires Spring Boot Actuator dependency

## 🔄 Updates and Maintenance

### Updating Dependencies
```bash
# Check for dependency updates
./gradlew dependencyUpdates

# Update Gradle wrapper
./gradlew wrapper --gradle-version=latest
```

### Database Schema Updates
- Add new migration files to `src/main/resources/db/migration/`
- Follow Flyway naming conventions
- Test migrations on a copy of production data first

---

**Happy Developing! 🚀**

*Last Updated: October 17, 2025*  
*Version: 1.0*  
*For DevSwipe Spring Boot Backend*