-- Enable UUID generation (for PostgreSQL)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =========================
-- Users table
-- =========================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL, -- store hashed password (bcrypt, argon2, etc.)
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);

-- =========================
-- Projects table
-- =========================
CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    difficulty VARCHAR(50),
    full_description TEXT NOT NULL,
    github_link VARCHAR(255),
    preview_description TEXT NOT NULL,
    tags TEXT[] NOT NULL, -- PostgreSQL array
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_projects_created_by ON projects(created_by);
CREATE INDEX idx_projects_tags ON projects USING GIN (tags);

-- =========================
-- User profiles table
-- =========================
CREATE TABLE user_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    bio TEXT,
    interests TEXT[], -- PostgreSQL array
    name VARCHAR(100) NOT NULL,
    onboarding_completed BOOLEAN DEFAULT FALSE,
    profile_image_url TEXT,
    skills TEXT[], -- PostgreSQL array
    university VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);
CREATE INDEX idx_user_profiles_interests ON user_profiles USING GIN (interests);
CREATE INDEX idx_user_profiles_skills ON user_profiles USING GIN (skills);
