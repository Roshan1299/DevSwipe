# Database Schema

This document outlines the Firebase Firestore database structure for **ProjectSwipe** (also referred to as DevSwipe), a Tinder-like Android application for discovering side project ideas and connecting with collaborators among university students.

## Database Structure Overview

### Current MVP Structure
```
projectswipe-db/
├── users/                 # User authentication and profile data
└── project_ideas/         # Project ideas and details
```

### Future Full App Structure
```
projectswipe-db/
├── users/                 # User authentication and profile data
├── project_ideas/         # Project ideas and details
├── collaboration_posts/   # Posts seeking collaborators
├── matches/              # User-project and user-user matches
├── messages/             # In-app messaging system
├── swipes/               # User swipe history and preferences
├── notifications/        # Push notification management
└── universities/         # University domain verification
```

## Current MVP Collections

### 1. Users Collection (`users/`)

Stores user authentication data and profile information for the MVP version.

**Collection Path:** `/users/{userId}`

#### Current MVP Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `bio` | `string` | No | User's biography/description |
| `createdAt` | `number` | Yes | Unix timestamp of account creation |
| `email` | `string` | Yes | User's email address |
| `interests` | `array` | Yes | Array of user interests (selected during post-registration setup) |
| `name` | `string` | Yes | User's display name |
| `profileImageUrl` | `string` | No | URL to user's profile image (empty string if none) |
| `skills` | `array` | Yes | Array of user's technical skills (selected during post-registration setup) |
| `university` | `string` | No | User's university affiliation |

#### Example MVP Document

```json
{
  "bio": "Computer Science student passionate about AI and web development",
  "createdAt": 1753057154711,
  "email": "student@ualberta.ca",
  "interests": ["AI", "Design", "Startups"],
  "name": "John Doe",
  "profileImageUrl": "",
  "skills": ["Flutter", "Python", "React"],
  "university": "University of Alberta"
}
```

### 2. Project Ideas Collection (`project_ideas/`)

Stores project ideas for the swipe interface in the MVP version.

**Collection Path:** `/project_ideas/{projectId}`

#### Current MVP Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `createdBy` | `string` | Yes | User ID of the project creator |
| `difficulty` | `string` | Yes | Project difficulty level ("Beginner", "Intermediate", "Advanced") |
| `fullDescription` | `string` | Yes | Complete project description |
| `id` | `string` | Yes | Unique project identifier |
| `previewDescription` | `string` | Yes | Short description for card preview |
| `tags` | `array` | Yes | Array of technology/skill tags |
| `title` | `string` | Yes | Project title |

#### Example MVP Document

```json
{
  "createdBy": "2FwXbGT4DZeJR2s2h4PitYoACz32",
  "difficulty": "Beginner",
  "fullDescription": "Build a modern todo app with React and Firebase. Perfect for learning frontend development and database integration.",
  "id": "KJPSbmPYSj8QvHlb0Ose",
  "previewDescription": "React Todo App with Firebase backend",
  "tags": ["React", "Firebase", "JavaScript", "CSS"],
  "title": "Modern Todo Application"
}
```

## Future Full App Schema

### Enhanced Users Collection

#### Additional Future Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `lastActive` | `timestamp` | No | Last app activity timestamp |
| `preferences` | `object` | No | User matching and notification preferences |
| `swipeHistory` | `array` | No | Array of swiped project IDs with actions |
| `collaborationCount` | `number` | No | Number of active collaborations |
| `projectsCreated` | `number` | No | Total projects created by user |
| `verified` | `boolean` | No | University email verification status |
| `pushTokens` | `array` | No | FCM push notification tokens |

#### Preferences Object Structure
```json
{
  "preferences": {
    "maxDifficulty": "Advanced",
    "preferredTags": ["React", "Python", "AI"],
    "collaborationOpen": true,
    "notificationsEnabled": true,
    "emailNotifications": false
  }
}
```

### New Future Collections

#### 3. Collaboration Posts Collection (`collaboration_posts/`)

For users seeking collaborators on existing projects.

**Collection Path:** `/collaboration_posts/{postId}`

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `string` | Yes | Unique post identifier |
| `createdBy` | `string` | Yes | User ID of post creator |
| `projectTitle` | `string` | Yes | Title of the collaboration project |
| `description` | `string` | Yes | Detailed project description |
| `skillsNeeded` | `array` | Yes | Required skills for collaborators |
| `timeCommitment` | `string` | Yes | Expected time commitment |
| `teamSize` | `number` | Yes | Desired number of collaborators |
| `currentTeamSize` | `number` | Yes | Current number of team members |
| `deadline` | `timestamp` | No | Project deadline |
| `status` | `string` | Yes | "active", "filled", "completed", "cancelled" |
| `createdAt` | `timestamp` | Yes | Post creation timestamp |
| `updatedAt` | `timestamp` | Yes | Last update timestamp |

#### 4. Matches Collection (`matches/`)

Stores successful matches between users and projects/collaborators.

**Collection Path:** `/matches/{matchId}`

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `string` | Yes | Unique match identifier |
| `userId` | `string` | Yes | ID of the user who swiped |
| `targetId` | `string` | Yes | ID of project or collaboration post |
| `targetType` | `string` | Yes | "project_idea" or "collaboration_post" |
| `status` | `string` | Yes | "pending", "accepted", "declined", "expired" |
| `createdAt` | `timestamp` | Yes | Match creation timestamp |
| `expiresAt` | `timestamp` | Yes | Match expiration timestamp |

#### 5. Messages Collection (`messages/`)

In-app messaging system for collaboration discussions.

**Collection Path:** `/messages/{conversationId}/messages/{messageId}`

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `string` | Yes | Unique message identifier |
| `senderId` | `string` | Yes | ID of message sender |
| `receiverId` | `string` | Yes | ID of message receiver |
| `content` | `string` | Yes | Message content |
| `timestamp` | `timestamp` | Yes | Message sent timestamp |
| `read` | `boolean` | Yes | Message read status |
| `type` | `string` | Yes | "text", "image", "file" |

#### 6. Swipes Collection (`swipes/`)

Track user swipe actions for recommendation algorithm.

**Collection Path:** `/swipes/{swipeId}`

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `userId` | `string` | Yes | ID of user who swiped |
| `targetId` | `string` | Yes | ID of swiped item |
| `targetType` | `string` | Yes | "project_idea" or "collaboration_post" |
| `action` | `string` | Yes | "like" (right swipe) or "pass" (left swipe) |
| `timestamp` | `timestamp` | Yes | Swipe timestamp |

#### 7. Notifications Collection (`notifications/`)

Push notification management and history.

**Collection Path:** `/notifications/{notificationId}`

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `string` | Yes | Unique notification identifier |
| `userId` | `string` | Yes | Target user ID |
| `title` | `string` | Yes | Notification title |
| `body` | `string` | Yes | Notification content |
| `type` | `string` | Yes | "match", "message", "system", "update" |
| `data` | `object` | No | Additional notification data |
| `sent` | `boolean` | Yes | Delivery status |
| `read` | `boolean` | Yes | Read status |
| `createdAt` | `timestamp` | Yes | Notification creation timestamp |

#### 8. Universities Collection (`universities/`)

University domain verification for enhanced features.

**Collection Path:** `/universities/{universityId}`

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | `string` | Yes | Unique university identifier |
| `name` | `string` | Yes | University full name |
| `domain` | `string` | Yes | Email domain (e.g., "ualberta.ca") |
| `verified` | `boolean` | Yes | Verification status |
| `location` | `string` | No | University location |
| `studentCount` | `number` | No | Number of registered students |

## Data Relationships

### Current MVP Relationships
- **User → Project Ideas**: One-to-Many (`project_ideas.createdBy` → `users.{userId}`)

### Future Full App Relationships
- **User → Collaboration Posts**: One-to-Many
- **User → Matches**: One-to-Many (as both matcher and target)
- **User → Messages**: One-to-Many (as both sender and receiver)
- **User → Swipes**: One-to-Many
- **User → Notifications**: One-to-Many
- **Project Ideas → Matches**: One-to-Many
- **Collaboration Posts → Matches**: One-to-Many

## Security Rules

### MVP Security Implementation
```javascript
// Users collection - MVP rules
match /users/{userId} {
  allow read, write: if request.auth != null && request.auth.uid == userId;
  allow read: if request.auth != null; // For profile discovery
}

// Project ideas collection - MVP rules
match /project_ideas/{projectId} {
  allow read: if request.auth != null;
  allow create: if request.auth != null;
  allow update, delete: if request.auth != null && resource.data.createdBy == request.auth.uid;
}
```

### Future Enhanced Security Rules
```javascript
// Enhanced security with role-based access and advanced validation
match /matches/{matchId} {
  allow read, write: if request.auth != null && 
    (resource.data.userId == request.auth.uid || 
     resource.data.targetUserId == request.auth.uid);
}

match /messages/{conversationId}/messages/{messageId} {
  allow read, write: if request.auth != null && 
    (resource.data.senderId == request.auth.uid || 
     resource.data.receiverId == request.auth.uid);
}
```

## Indexing Strategy

### MVP Indexes
1. **Project Ideas by Difficulty**: `difficulty ASC`
2. **Project Ideas by Creator**: `createdBy ASC`
3. **Project Ideas by Tags**: `tags ARRAY_CONTAINS_ANY`

### Future Full App Indexes
1. **Matches by User and Status**: `userId ASC, status ASC`
2. **Messages by Conversation**: `conversationId ASC, timestamp ASC`
3. **Swipes by User**: `userId ASC, timestamp DESC`
4. **Notifications by User and Read Status**: `userId ASC, read ASC, createdAt DESC`
5. **Collaboration Posts by Status and Skills**: `status ASC, skillsNeeded ARRAY_CONTAINS_ANY`

## Migration Strategy

### Phase 1: MVP (Current)
- Firebase Firestore with basic collections
- Simple authentication and project management
- Basic swipe functionality

### Phase 2: Enhanced Features
- Add collaboration posts and matching system
- Implement in-app messaging
- Enhanced user profiles and preferences

### Phase 3: PostgreSQL Migration
```sql
-- Example PostgreSQL schema structure
CREATE TABLE users (
  id UUID PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  bio TEXT,
  university VARCHAR(255),
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE project_ideas (
  id UUID PRIMARY KEY,
  created_by UUID REFERENCES users(id),
  title VARCHAR(255) NOT NULL,
  preview_description TEXT NOT NULL,
  full_description TEXT NOT NULL,
  difficulty VARCHAR(50) NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);
```

### Phase 4: Advanced Features
- Machine learning recommendation engine
- Advanced analytics and insights
- University integration and verification

## Performance Considerations

### MVP Optimization
- Implement pagination for project cards (20 cards per batch)
- Cache user profiles for offline viewing
- Optimize image loading and caching

### Future Optimization
- Implement Redis caching layer
- Database query optimization
- CDN for static assets
- Real-time data synchronization optimization

## Backup and Recovery

### MVP Strategy
- Firebase automatic backups
- Weekly manual exports
- Version control for schema changes

### Future Strategy
- Automated PostgreSQL backups
- Point-in-time recovery
- Cross-region data replication
- Disaster recovery procedures

---

*Last Updated: July 23, 2025*  
*Version: 2.0 (MVP + Future Schema)*  
*Target Migration: Post-MVP Phase*
