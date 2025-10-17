package com.first.projectswipe.data.models

import com.first.projectswipe.network.dto.UserDto
import java.util.UUID

data class CollabPost(
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
