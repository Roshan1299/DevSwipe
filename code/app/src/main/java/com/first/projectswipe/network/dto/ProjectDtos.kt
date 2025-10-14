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

data class CollaborationCreateRequest(
    val projectTitle: String,
    val description: String,
    val skillsNeeded: List<String>,
    val timeCommitment: String,
    val teamSize: Int
)

data class CollaborationResponse(
    val id: UUID,
    val projectTitle: String,
    val description: String,
    val skillsNeeded: List<String>,
    val timeCommitment: String,
    val teamSize: Int,
    val currentTeamSize: Int,
    val status: String,
    val createdBy: UserDto,
    val createdAt: Long,
    val updatedAt: Long
)

data class UpdateCollaborationRequest(
    val projectTitle: String? = null,
    val description: String? = null,
    val skillsNeeded: List<String>? = null,
    val timeCommitment: String? = null,
    val teamSize: Int? = null
)
