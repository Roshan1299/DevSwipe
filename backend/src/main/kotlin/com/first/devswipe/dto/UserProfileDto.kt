package com.first.devswipe.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.first.devswipe.entity.UserProfile
import java.time.format.DateTimeFormatter

data class UserProfileResponse(
    val id: String,
    @JsonProperty("userId")
    val userId: String,
    val name: String,
    val bio: String? = null,
    val skills: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val university: String? = null,
    @JsonProperty("profileImageUrl")
    val profileImageUrl: String? = null,
    @JsonProperty("onboardingCompleted")
    val onboardingCompleted: Boolean = false,
    @JsonProperty("createdAt")
    val createdAt: String,
    @JsonProperty("updatedAt")
    val updatedAt: String
)

// Extension function to convert UserProfile entity to UserProfileResponse DTO
fun UserProfile.toUserProfileResponse(): UserProfileResponse {
    return UserProfileResponse(
        id = this.id.toString(),
        userId = this.userId.toString(),
        name = this.name,
        bio = this.bio,
        skills = this.skills?.toList() ?: emptyList(),
        interests = this.interests?.toList() ?: emptyList(),
        university = this.university,
        profileImageUrl = this.profileImageUrl,
        onboardingCompleted = this.onboardingCompleted,
        createdAt = this.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        updatedAt = this.updatedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )
}