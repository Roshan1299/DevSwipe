package com.first.projectswipe.network.dto

import java.util.UUID

data class ProjectCreateRequest(
    val title: String,
    val previewDescription: String,
    val fullDescription: String,
    val githubLink: String?,
    val tags: List<String>,
    val difficulty: String
)

data class ProjectResponse(
    val id: UUID,
    val title: String,
    val previewDescription: String,
    val fullDescription: String,
    val githubLink: String?,
    val tags: List<String>,
    val difficulty: String,
    val createdBy: UserDto
)

data class UpdateProjectRequest(
    val title: String? = null,
    val fullDescription: String? = null,
    val previewDescription: String? = null,
    val tags: List<String>? = null,
    val difficulty: String? = null,
    val githubLink: String? = null
)
