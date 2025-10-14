-- Create collab_posts table
CREATE TABLE collab_posts (
    id UUID PRIMARY KEY,
    project_title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    time_commitment VARCHAR(255) NOT NULL,
    team_size INTEGER NOT NULL,
    current_team_size INTEGER DEFAULT 0,
    status VARCHAR(50) DEFAULT 'active',
    created_by UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Create collab_post_skills table for the skills needed
CREATE TABLE collab_post_skills (
    collab_post_id UUID NOT NULL,
    skill VARCHAR(255),
    FOREIGN KEY (collab_post_id) REFERENCES collab_posts(id) ON DELETE CASCADE
);