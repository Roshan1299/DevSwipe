# DevSwipe API Reference

## 📖 Overview

This document provides a comprehensive reference for the DevSwipe backend API. All API endpoints are accessible at `http://localhost:8080/api/` during development and should be prefixed with your production domain in production.

## 🔐 Authentication

Most endpoints require authentication via JWT tokens. Include the token in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

## 📊 API Endpoints

### Authentication

#### Register User
```
POST /api/auth/register
```

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe",
  "university": "University of Example"
}
```

**Response:**
```json
{
  "token": "jwt-token-string",
  "user": {
    "id": "uuid-string",
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "university": "University of Example",
    "onboardingCompleted": false
  },
  "message": "Registration successful"
}
```

#### Login User
```
POST /api/auth/login
```

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```

**Response:**
```json
{
  "token": "jwt-token-string",
  "user": {
    "id": "uuid-string",
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "university": "University of Example",
    "onboardingCompleted": false
  },
  "message": "Login successful"
}
```

#### Get Current User
```
GET /api/auth/me
```

**Response:**
```json
{
  "id": "uuid-string",
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "university": "University of Example",
  "onboardingCompleted": false
}
```

### Projects

#### Get All Projects
```
GET /api/projects
```

**Response:**
```json
[
  {
    "id": "uuid-string",
    "title": "Project Title",
    "previewDescription": "Short description for card preview",
    "fullDescription": "Detailed project description",
    "githubLink": "https://github.com/...",
    "tags": ["tag1", "tag2"],
    "difficulty": "Beginner",
    "createdBy": {
      "id": "user-uuid",
      "username": "username",
      "email": "email@example.com",
      "firstName": "First",
      "lastName": "Last",
      "university": "University"
    }
  }
]
```

#### Get User's Projects
```
GET /api/projects/my-projects
```

**Response:**
```json
[
  {
    "id": "uuid-string",
    "title": "Project Title",
    "previewDescription": "Short description for card preview",
    "fullDescription": "Detailed project description",
    "githubLink": "https://github.com/...",
    "tags": ["tag1", "tag2"],
    "difficulty": "Beginner",
    "createdBy": {
      "id": "user-uuid",
      "username": "username",
      "email": "email@example.com",
      "firstName": "First",
      "lastName": "Last",
      "university": "University"
    }
  }
]
```

#### Create Project
```
POST /api/projects
```

**Request Body:**
```json
{
  "title": "New Project",
  "previewDescription": "Short description for card preview",
  "fullDescription": "Detailed project description",
  "githubLink": "https://github.com/...",
  "tags": ["tag1", "tag2"],
  "difficulty": "Beginner"
}
```

**Response:**
```json
{
  "id": "uuid-string",
  "title": "New Project",
  "previewDescription": "Short description for card preview",
  "fullDescription": "Detailed project description",
  "githubLink": "https://github.com/...",
  "tags": ["tag1", "tag2"],
  "difficulty": "Beginner",
  "createdBy": {
    "id": "user-uuid",
    "username": "username",
    "email": "email@example.com",
    "firstName": "First",
    "lastName": "Last",
    "university": "University"
  }
}
```

#### Update Project
```
PUT /api/projects/{projectId}
```

**Request Body:**
```json
{
  "title": "Updated Project",
  "previewDescription": "Updated short description",
  "fullDescription": "Updated detailed project description",
  "githubLink": "https://github.com/updated...",
  "tags": ["updated-tag1", "updated-tag2"],
  "difficulty": "Intermediate"
}
```

**Response:**
Response is the same as the project creation endpoint with updated values.

#### Delete Project
```
DELETE /api/projects/{projectId}
```

**Response:**
```json
{
  "message": "Project deleted successfully"
}
```

#### Filter Projects
```
GET /api/projects/filter?difficulty=Beginner&tags=React&tags=Node.js
```

**Query Parameters:**
- `difficulty` (optional): Filter by difficulty level (Beginner, Intermediate, Advanced)
- `tags` (optional): Filter by tags (can specify multiple tags)

**Response:**
Same as Get All Projects but filtered.

### User Profiles

#### Get User Profile
```
GET /api/users/{userId}
```

**Response:**
```json
{
  "id": "uuid-string",
  "userId": "user-uuid",
  "bio": "User biography",
  "interests": ["interest1", "interest2"],
  "name": "Full Name",
  "onboardingCompleted": true,
  "profileImageUrl": "https://example.com/image.jpg",
  "skills": ["skill1", "skill2"],
  "university": "University Name",
  "createdAt": "2023-01-15T10:30:00",
  "updatedAt": "2023-01-15T10:30:00"
}
```

#### Update User Profile
```
PUT /api/users/{userId}
```

**Request Body:**
```json
{
  "bio": "Updated biography",
  "interests": ["updated-interest1", "updated-interest2"],
  "name": "Updated Full Name",
  "profileImageUrl": "https://example.com/updated-image.jpg",
  "skills": ["updated-skill1", "updated-skill2"],
  "university": "Updated University Name"
}
```

#### Update User Skills
```
PATCH /api/users/{userId}/skills
```

**Request Body:**
```json
{
  "skills": ["skill1", "skill2", "skill3"]
}
```

#### Update User Interests
```
PATCH /api/users/{userId}/interests
```

**Request Body:**
```json
{
  "interests": ["interest1", "interest2", "interest3"]
}
```

#### Complete Onboarding
```
PATCH /api/users/{userId}/onboarding
```

**Response:**
```json
{
  "message": "Onboarding completed successfully"
}
```

### Collaboration Posts

#### Get All Collaboration Posts
```
GET /api/collaborations
```

**Response:**
```json
[
  {
    "id": "uuid-string",
    "projectTitle": "Collaboration Project",
    "description": "Detailed description",
    "skillsNeeded": ["skill1", "skill2"],
    "timeCommitment": "5-10 hours/week",
    "teamSize": 4,
    "currentTeamSize": 1,
    "status": "active",
    "createdBy": {
      "id": "user-uuid",
      "username": "username",
      "email": "email@example.com",
      "firstName": "First",
      "lastName": "Last",
      "university": "University"
    },
    "createdAt": "2023-01-15T10:30:00",
    "updatedAt": "2023-01-15T10:30:00"
  }
]
```

#### Get User's Collaboration Posts
```
GET /api/collaborations/my-collaborations
```

#### Create Collaboration Post
```
POST /api/collaborations
```

**Request Body:**
```json
{
  "projectTitle": "Looking for React Developer",
  "description": "Building a React application with Spring Boot backend",
  "skillsNeeded": ["React", "JavaScript", "CSS"],
  "timeCommitment": "5-10 hours/week",
  "teamSize": 3
}
```

#### Update Collaboration Post
```
PUT /api/collaborations/{postId}
```

**Request Body:**
```json
{
  "projectTitle": "Updated Project Title",
  "description": "Updated description",
  "skillsNeeded": ["updated-skill1", "updated-skill2"],
  "timeCommitment": "10-15 hours/week",
  "teamSize": 4
}
```

#### Delete Collaboration Post
```
DELETE /api/collaborations/{postId}
```

**Response:**
```json
{
  "message": "Collaboration post deleted successfully"
}
```

### File Upload

#### Upload File
```
POST /api/upload
Content-Type: multipart/form-data

file: <file-data>
```

**Response:**
```json
{
  "message": "File uploaded successfully",
  "url": "/uploads/filename.jpg"
}
```

## 🛡️ Error Responses

All error responses follow the same structure:

```json
{
  "error": "Error message describing the issue",
  "timestamp": "2023-01-15T10:30:00",
  "status": 400,
  "path": "/api/endpoint"
}
```

### Common HTTP Status Codes

- `200 OK`: Request successful
- `201 Created`: Resource created successfully
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Authentication required or token invalid
- `403 Forbidden`: Access denied to the resource
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

## 📝 Response Status Codes

### Success Codes
- `200`: Standard response for successful HTTP requests
- `201`: Resource successfully created
- `204`: Request successful but no content returned

### Client Error Codes
- `400`: Bad request - invalid input data
- `401`: Unauthorized - authentication required
- `403`: Forbidden - access not allowed
- `404`: Not found - resource does not exist
- `409`: Conflict - resource already exists

### Server Error Codes
- `500`: Internal server error
- `502`: Bad gateway
- `503`: Service unavailable

## 🔄 Rate Limiting

The API implements rate limiting:
- Up to 100 requests per hour per IP address
- Up to 25 requests per 15-minute window per authenticated user

## 🔍 Pagination

For endpoints that return lists, pagination is supported through query parameters:
- `page`: Page number (0-indexed)
- `size`: Number of items per page (default: 20, max: 100)

Example:
```
GET /api/projects?page=1&size=10
```

## 🏷️ Filtering

Endpoints that return lists support filtering via query parameters.

## 📅 Date Format

Dates are returned in ISO 8601 format: `YYYY-MM-DDTHH:MM:SS.SSSZ`

## 🆘 Support

For API support or issues:
- Create an issue in the GitHub repository
- Contact the development team through the official channels
- Check the logs for detailed error information

---

**API Version**: 1.0  
**Last Updated**: October 17, 2025  
**Base URL**: `http://localhost:8080/api/` (development)