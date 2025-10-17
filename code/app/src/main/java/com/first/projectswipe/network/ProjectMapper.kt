package com.first.projectswipe.network

import com.first.projectswipe.data.models.ProjectIdea
import com.first.projectswipe.data.models.CollabPost
import com.first.projectswipe.network.dto.ProjectResponse
import com.first.projectswipe.network.dto.CollaborationResponse

fun ProjectResponse.toProjectIdea(): ProjectIdea {
    return ProjectIdea(
        id = this.id,
        title = this.title,
        previewDescription = this.previewDescription,
        fullDescription = this.fullDescription,
        githubLink = this.githubLink,
        tags = this.tags,
        difficulty = this.difficulty,
        createdBy = this.createdBy
    )
}

fun CollaborationResponse.toCollabPost(): CollabPost {
    return CollabPost(
        id = this.id,
        projectTitle = this.projectTitle,
        description = this.description,
        skillsNeeded = this.skillsNeeded,
        timeCommitment = this.timeCommitment,
        teamSize = this.teamSize,
        currentTeamSize = this.currentTeamSize,
        status = this.status,
        createdBy = this.createdBy,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
