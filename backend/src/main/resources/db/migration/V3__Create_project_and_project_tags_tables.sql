DROP TABLE IF EXISTS project_tags;
DROP TABLE IF EXISTS projects;

CREATE TABLE projects (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    preview_description VARCHAR(255) NOT NULL,
    full_description TEXT NOT NULL,
    github_link VARCHAR(255),
    difficulty VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE project_tags (
    project_id UUID NOT NULL,
    tags VARCHAR(255),
    FOREIGN KEY (project_id) REFERENCES projects(id)
);