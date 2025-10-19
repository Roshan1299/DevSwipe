# Database Schema

This document outlines the PostgreSQL database structure for **DevSwipe** (formerly ProjectSwipe), a Tinder-like Android application for discovering side project ideas and connecting with collaborators among university students. The application uses a Spring Boot backend with PostgreSQL database for data persistence.

## Database Structure Overview

### Current Structure
```
devswipe_db/
├── users/                 # User authentication and profile data
├── user_profiles/         # Extended user profile information
├── projects/              # Project ideas and details
└── collab_posts/          # Posts seeking collaborators
```

### Future Full App Structure
```
devswipe_db/
├── users/                 # User authentication and profile data
├── user_profiles/         # Extended user profile information
├── projects/              # Project ideas and details
├── collab_posts/          # Posts seeking collaborators
├── matches/               # User-project and user-user matches
├── messages/              # In-app messaging system
├── swipes/                # User swipe history and preferences
├── notifications/         # Push notification management
└── universities/          # University domain verification
```

## Current Tables

### 1. Users Table (`users`)

Stores user authentication data and basic profile information for the current version.

**Table:** `users`

#### Current Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `UUID` | Yes | Unique user identifier (Primary Key) |
| `username` | `VARCHAR(255)` | Yes | User's display name (Unique) |
| `email` | `VARCHAR(255)` | Yes | User's email address (Unique) |
| `password_hash` | `VARCHAR(255)` | Yes | Hashed password for authentication |
| `first_name` | `VARCHAR(255)` | No | User's first name |
| `last_name` | `VARCHAR(255)` | No | User's last name |
| `created_at` | `TIMESTAMP` | Yes | Timestamp of account creation |
| `updated_at` | `TIMESTAMP` | Yes | Timestamp of last update |
| `profile_picture_url` | `TEXT` | No | URL to user's profile image |
| `bio` | `TEXT` | No | User's biography/description |
| `university` | `VARCHAR(255)` | No | User's university affiliation |

#### Example Row

```sql
INSERT INTO users (id, username, email, password_hash, first_name, last_name, created_at, updated_at) 
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'johndoe', 'john.doe@ualberta.ca', '$2a$10$...', 'John', 'Doe', '2025-01-15 10:30:00', '2025-01-15 10:30:00');
```

### 2. User Profiles Table (`user_profiles`)

Stores extended user profile information including skills and interests.

**Table:** `user_profiles`

#### Current Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `UUID` | Yes | Unique profile identifier (Primary Key) |
| `user_id` | `UUID` | Yes | Reference to the user (Foreign Key -> users.id) |
| `bio` | `TEXT` | No | User's biography/description |
| `interests` | `TEXT[]` | No | Array of user interests |
| `name` | `VARCHAR(255)` | Yes | User's display name |
| `onboarding_completed` | `BOOLEAN` | Yes | Whether user completed onboarding (default: false) |
| `profile_image_url` | `TEXT` | No | URL to user's profile image |
| `skills` | `TEXT[]` | No | Array of user's technical skills |
| `university` | `VARCHAR(255)` | No | User's university affiliation |
| `created_at` | `TIMESTAMP` | Yes | Timestamp of profile creation |
| `updated_at` | `TIMESTAMP` | Yes | Timestamp of last update |

#### Example Row

```sql
INSERT INTO user_profiles (id, user_id, bio, interests, name, onboarding_completed, skills, university, created_at, updated_at) 
VALUES ('660e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440000', 'Computer Science student passionate about AI and web development', '{"AI", "Design", "Startups"}', 'John Doe', true, '{"Flutter", "Python", "React"}', 'University of Alberta', '2025-01-15 10:30:00', '2025-01-15 10:30:00');
```

### 3. Projects Table (`projects`)

Stores project ideas for the swipe interface in the current version.

**Table:** `projects`

#### Current Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `UUID` | Yes | Unique project identifier (Primary Key) |
| `title` | `VARCHAR(255)` | Yes | Project title |
| `preview_description` | `TEXT` | Yes | Short description for card preview |
| `full_description` | `TEXT` | Yes | Complete project description |
| `github_link` | `TEXT` | No | URL to the project's GitHub repository |
| `tags` | `TEXT[]` | Yes | Array of technology/skill tags |
| `difficulty` | `VARCHAR(50)` | Yes | Project difficulty level ("Beginner", "Intermediate", "Advanced") |
| `user_id` | `UUID` | Yes | Reference to the user who created the project (Foreign Key -> users.id) |

#### Example Row

```sql
INSERT INTO projects (id, title, preview_description, full_description, github_link, tags, difficulty, user_id) 
VALUES ('770e8400-e29b-41d4-a716-446655440000', 'Modern Todo Application', 'React Todo App with Spring Boot backend', 'Build a modern todo app with React and Spring Boot. Perfect for learning frontend development and backend integration.', 'https://github.com/user/react-todo-app', '{"React", "Spring Boot", "JavaScript", "CSS"}', 'Beginner', '550e8400-e29b-41d4-a716-446655440000');
```

### 4. Collaborator Posts Table (`collab_posts`)

For users seeking collaborators on existing projects.

**Table:** `collab_posts`

#### Current Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `UUID` | Yes | Unique post identifier (Primary Key) |
| `project_title` | `VARCHAR(255)` | Yes | Title of the collaboration project |
| `description` | `TEXT` | Yes | Detailed project description |
| `skills_needed` | `TEXT[]` | Yes | Required skills for collaborators |
| `time_commitment` | `VARCHAR(255)` | Yes | Expected time commitment |
| `team_size` | `INTEGER` | Yes | Desired number of collaborators |
| `current_team_size` | `INTEGER` | Yes | Current number of team members (default: 0) |
| `status` | `VARCHAR(50)` | Yes | "active", "filled", "completed", "cancelled" (default: "active") |
| `created_at` | `TIMESTAMP` | Yes | Post creation timestamp |
| `updated_at` | `TIMESTAMP` | Yes | Last update timestamp |
| `user_id` | `UUID` | Yes | Reference to the user who created the post (Foreign Key -> users.id) |

#### Example Row

```sql
INSERT INTO collab_posts (id, project_title, description, skills_needed, time_commitment, team_size, current_team_size, status, created_at, updated_at, user_id) 
VALUES ('880e8400-e29b-41d4-a716-446655440000', 'Mobile Game Development', 'Create a cross-platform mobile game using React Native', '{"React Native", "Game Design", "Unity"}', '5-10 hours/week', 4, 1, 'active', '2025-01-15 10:30:00', '2025-01-15 10:30:00', '550e8400-e29b-41d4-a716-446655440000');
```

## Future Full App Schema

### Enhanced User Tables

#### Additional Future Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `last_active` | `TIMESTAMP` | No | Last app activity timestamp |
| `collaboration_count` | `INTEGER` | No | Number of active collaborations |
| `projects_created` | `INTEGER` | No | Total projects created by user |
| `verified` | `BOOLEAN` | No | University email verification status |

### New Future Tables

#### 5. Matches Table (`matches`)

Stores successful matches between users and projects/collaborators.

**Table:** `matches`

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `UUID` | Yes | Unique match identifier (Primary Key) |
| `user_id` | `UUID` | Yes | ID of the user who swiped (Foreign Key -> users.id) |
| `target_id` | `UUID` | Yes | ID of project or collaboration post |
| `target_type` | `VARCHAR(50)` | Yes | "project" or "collab_post" |
| `status` | `VARCHAR(50)` | Yes | "pending", "accepted", "declined", "expired" |
| `created_at` | `TIMESTAMP` | Yes | Match creation timestamp |
| `expires_at` | `TIMESTAMP` | Yes | Match expiration timestamp |

#### 6. Messages Table (`messages`)

In-app messaging system for collaboration discussions.

**Table:** `messages`

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `UUID` | Yes | Unique message identifier (Primary Key) |
| `sender_id` | `UUID` | Yes | ID of message sender (Foreign Key -> users.id) |
| `receiver_id` | `UUID` | Yes | ID of message receiver (Foreign Key -> users.id) |
| `content` | `TEXT` | Yes | Message content |
| `timestamp` | `TIMESTAMP` | Yes | Message sent timestamp |
| `read` | `BOOLEAN` | Yes | Message read status (default: false) |
| `type` | `VARCHAR(50)` | Yes | "text", "image", "file" (default: "text") |

#### 7. Swipes Table (`swipes`)

Track user swipe actions for recommendation algorithm.

**Table:** `swipes`

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `UUID` | Yes | Unique swipe identifier (Primary Key) |
| `user_id` | `UUID` | Yes | ID of user who swiped (Foreign Key -> users.id) |
| `target_id` | `UUID` | Yes | ID of swiped item |
| `target_type` | `VARCHAR(50)` | Yes | "project" or "collab_post" |
| `action` | `VARCHAR(50)` | Yes | "like" (right swipe) or "pass" (left swipe) |
| `timestamp` | `TIMESTAMP` | Yes | Swipe timestamp |

#### 8. Notifications Table (`notifications`)

Push notification management and history.

**Table:** `notifications`

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `UUID` | Yes | Unique notification identifier (Primary Key) |
| `user_id` | `UUID` | Yes | Target user ID (Foreign Key -> users.id) |
| `title` | `VARCHAR(255)` | Yes | Notification title |
| `body` | `TEXT` | Yes | Notification content |
| `type` | `VARCHAR(50)` | Yes | "match", "message", "system", "update" |
| `data` | `JSONB` | No | Additional notification data as JSON |
| `sent` | `BOOLEAN` | Yes | Delivery status (default: false) |
| `read` | `BOOLEAN` | Yes | Read status (default: false) |
| `created_at` | `TIMESTAMP` | Yes | Notification creation timestamp |

#### 9. Universities Table (`universities`)

University domain verification for enhanced features.

**Table:** `universities`

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `UUID` | Yes | Unique university identifier (Primary Key) |
| `name` | `VARCHAR(255)` | Yes | University full name |
| `domain` | `VARCHAR(255)` | Yes | Email domain (e.g., "ualberta.ca") |
| `verified` | `BOOLEAN` | Yes | Verification status (default: false) |
| `location` | `VARCHAR(255)` | No | University location |
| `student_count` | `INTEGER` | No | Number of registered students |

## Data Relationships

### Current Relationships
- **Users → Projects**: One-to-Many (`projects.user_id` → `users.id`)
- **Users → UserProfiles**: One-to-One (`user_profiles.user_id` → `users.id`)
- **Users → CollabPosts**: One-to-Many (`collab_posts.user_id` → `users.id`)

### Future Full App Relationships
- **Users → Matches**: One-to-Many (as both matcher and target)
- **Users → Messages**: One-to-Many (as both sender and receiver)
- **Users → Swipes**: One-to-Many
- **Users → Notifications**: One-to-Many
- **Projects → Matches**: One-to-Many
- **CollabPosts → Matches**: One-to-Many

## SQL Schema Definition

```sql
-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    profile_picture_url TEXT,
    bio TEXT,
    university VARCHAR(255)
);

-- User profiles table
CREATE TABLE user_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    bio TEXT,
    interests TEXT[],
    name VARCHAR(255) NOT NULL,
    onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE,
    profile_image_url TEXT,
    skills TEXT[],
    university VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Projects table
CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    preview_description TEXT NOT NULL,
    full_description TEXT NOT NULL,
    github_link TEXT,
    tags TEXT[] NOT NULL,
    difficulty VARCHAR(50) NOT NULL CHECK (difficulty IN ('Beginner', 'Intermediate', 'Advanced')),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

-- Collaborator posts table
CREATE TABLE collab_posts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    skills_needed TEXT[] NOT NULL,
    time_commitment VARCHAR(255) NOT NULL,
    team_size INTEGER NOT NULL,
    current_team_size INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'active' CHECK (status IN ('active', 'filled', 'completed', 'cancelled')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_projects_user_id ON projects(user_id);
CREATE INDEX idx_projects_difficulty ON projects(difficulty);
CREATE INDEX idx_projects_tags ON projects USING GIN(tags);
CREATE INDEX idx_collab_posts_user_id ON collab_posts(user_id);
CREATE INDEX idx_collab_posts_status ON collab_posts(status);
CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);

-- Future tables (commented out until implemented)

/*
-- Matches table
CREATE TABLE matches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_id UUID NOT NULL,
    target_type VARCHAR(50) NOT NULL CHECK (target_type IN ('project', 'collab_post')),
    status VARCHAR(50) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'accepted', 'declined', 'expired')),
    created_at TIMESTAMP DEFAULT NOW(),
    expires_at TIMESTAMP NOT NULL
);

-- Messages table
CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    receiver_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT NOW(),
    read BOOLEAN NOT NULL DEFAULT FALSE,
    type VARCHAR(50) NOT NULL DEFAULT 'text' CHECK (type IN ('text', 'image', 'file'))
);

-- Swipes table
CREATE TABLE swipes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_id UUID NOT NULL,
    target_type VARCHAR(50) NOT NULL CHECK (target_type IN ('project', 'collab_post')),
    action VARCHAR(50) NOT NULL CHECK (action IN ('like', 'pass')),
    timestamp TIMESTAMP DEFAULT NOW()
);

-- Notifications table
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('match', 'message', 'system', 'update')),
    data JSONB,
    sent BOOLEAN NOT NULL DEFAULT FALSE,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Universities table
CREATE TABLE universities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    domain VARCHAR(255) NOT NULL UNIQUE,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    location VARCHAR(255),
    student_count INTEGER
);
*/
```

## Authentication and Security

### User Authentication
- Passwords are stored using BCrypt hashing
- JWT tokens are used for session management
- Spring Security provides authentication and authorization

### Access Controls
- Users can only view and modify their own data
- Projects can only be modified by their creators
- Profile information is restricted based on privacy settings

## Indexing Strategy

### Current Indexes
1. **Projects by User ID**: `idx_projects_user_id` - for user-specific project queries
2. **Projects by Difficulty**: `idx_projects_difficulty` - for filtering projects by difficulty
3. **Projects by Tags**: `idx_projects_tags` - for searching projects by tags using GIN index
4. **Collab Posts by User ID**: `idx_collab_posts_user_id` - for user-specific collab posts
5. **Collab Posts by Status**: `idx_collab_posts_status` - for filtering active posts
6. **User Profiles by User ID**: `idx_user_profiles_user_id` - for profile lookups

### Future Performance Indexes
1. **Matches by User and Status**: `idx_matches_user_status`
2. **Messages by Conversation**: `idx_messages_conversation`
3. **Swipes by User**: `idx_swipes_user_timestamp`
4. **Notifications by User and Read Status**: `idx_notifications_user_read`

## Migration Strategy

### Phase 1: Current Implementation (Completed)
- PostgreSQL database with basic tables
- Spring Boot REST API for user authentication and project management
- Basic swipe functionality

### Phase 2: Enhanced Features
- Add collaboration posts and matching system
- Implement in-app messaging
- Enhanced user profiles and preferences

### Phase 3: Advanced Features
- Machine learning recommendation engine
- Advanced analytics and insights
- University integration and verification

## Performance Considerations

### Current Optimization
- Proper indexing on frequently queried columns
- Connection pooling for database connections
- Pagination for project cards (20 cards per batch)
- Efficient query design to minimize data transfer

### Future Optimization
- Redis caching layer for frequently accessed data
- Database query optimization with EXPLAIN ANALYZE
- CDN for static assets
- Optimized data retrieval patterns

## Backup and Recovery

### Current Strategy
- Automated PostgreSQL backups using pg_dump
- Point-in-time recovery enabled
- Regular backup verification
- Version control for schema changes using Flyway

### Recovery Procedures
- Automated backup restoration scripts
- Database cluster setup for high availability
- Regular disaster recovery testing

---

*Last Updated: October 17, 2025*  
*Version: 3.0 (Current PostgreSQL Schema)*  
*Platform: Spring Boot with PostgreSQL*
