package com.first.projectswipe.network

import com.first.projectswipe.data.models.ProjectIdea
import com.first.projectswipe.network.dto.ProjectResponse

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