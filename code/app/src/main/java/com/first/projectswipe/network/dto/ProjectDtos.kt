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
