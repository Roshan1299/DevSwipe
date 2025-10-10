package com.first.projectswipe.data.models

import com.first.projectswipe.network.dto.UserDto
import java.util.UUID

data class ProjectIdea(
    val id: UUID,
    val title: String,
    val previewDescription: String,
    val fullDescription: String,
    val githubLink: String?,
    val tags: List<String>,
    val difficulty: String,
    val createdBy: UserDto
)
