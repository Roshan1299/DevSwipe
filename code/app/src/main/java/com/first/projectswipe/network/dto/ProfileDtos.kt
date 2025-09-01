//package com.first.projectswipe.network.dto
//
//import com.google.gson.annotations.SerializedName
//import java.util.*
//
///**
// * Response DTO matching your backend UserProfile entity
// */
//data class UserProfileResponse(
//    val id: String,
//    @SerializedName("userId")
//    val userId: String,
//    val name: String,
//    val bio: String? = null,
//    val skills: List<String> = emptyList(), // Changed from nullable to empty list with default
//    val interests: List<String> = emptyList(), // Changed from nullable to empty list with default
//    val university: String? = null,
//    @SerializedName("profileImageUrl")
//    val profileImageUrl: String? = null,
//    @SerializedName("onboardingCompleted")
//    val onboardingCompleted: Boolean = false,
//    @SerializedName("createdAt")
//    val createdAt: String,
//    @SerializedName("updatedAt")
//    val updatedAt: String
//)
//
///**
// * UserDto - consolidated version for API responses
// * This replaces the duplicate definitions in AuthDtos.kt
// */
//data class UserDto(
//    val id: String, // Changed from UUID to String for consistency
//    val username: String,
//    val email: String,
//    val firstName: String? = null,
//    val lastName: String? = null,
//    val bio: String? = null,
//    val skills: List<String> = emptyList(),
//    val interests: List<String> = emptyList(),
//    val onboardingCompleted: Boolean = false,
//    val profileImageUrl: String? = null,
//    val createdAt: Long? = null,
//    val university: String? = null,
//    val updatedAt: String? = null
//)
//
///**
// * Extension function to convert UserDto to UserProfileResponse
// */
//fun UserDto.toUserProfileResponse(): UserProfileResponse {
//    return UserProfileResponse(
//        id = this.id,
//        userId = this.id, // Assuming userId is same as id
//        name = this.firstName?.let { firstName ->
//            this.lastName?.let { lastName ->
//                "$firstName $lastName"
//            } ?: firstName
//        } ?: this.username,
//        bio = this.bio,
//        skills = this.skills,
//        interests = this.interests,
//        university = this.university,
//        profileImageUrl = this.profileImageUrl,
//        onboardingCompleted = this.onboardingCompleted,
//        createdAt = this.createdAt?.toString() ?: "",
//        updatedAt = this.updatedAt ?: ""
//    )
//}
//
///**
// * Request DTO for creating/updating profile
// * Matches your backend UpdateProfileRequest
// */
//data class UpdateProfileRequest(
//    val name: String? = null,
//    val bio: String? = null,
//    val skills: List<String>? = null,
//    val interests: List<String>? = null,
//    val university: String? = null,
//    @SerializedName("profileImageUrl")
//    val profileImageUrl: String? = null,
//    @SerializedName("onboardingCompleted")
//    val onboardingCompleted: Boolean? = null
//)
//
///**
// * Request DTOs for specific updates - matching your ApiService
// * Note: UpdateUserRequest, UpdateSkillsRequest, and UpdateInterestsRequest are defined in AuthDtos.kt
// */
//
///**
// * Simplified profile for display purposes
// */
//data class ProfileSummary(
//    val id: String,
//    val name: String,
//    val university: String?,
//    val profileImageUrl: String?,
//    val skills: List<String> = emptyList()
//)
//
///**
// * For onboarding completion
// */
//data class OnboardingCompletionRequest(
//    val onboardingCompleted: Boolean = true
//)
//
///**
// * For profile search results
// */
//data class ProfileSearchResponse(
//    val profiles: List<UserProfileResponse>,
//    val totalCount: Int
//)

// network/dto/ProfileDtos.kt
package com.first.projectswipe.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO matching your backend UserProfile entity
 */
data class UserProfileResponse(
    val id: String,
    @SerializedName("userId")
    val userId: String,
    val name: String,
    val bio: String? = null,
    val skills: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val university: String? = null,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null,
    @SerializedName("onboardingCompleted")
    val onboardingCompleted: Boolean = false,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

/**
 * Extension function to convert UserDto to UserProfileResponse
 */
fun UserDto.toUserProfileResponse(): UserProfileResponse {
    return UserProfileResponse(
        id = this.id,
        userId = this.id, // Assuming userId is same as id
        name = this.firstName?.let { firstName ->
            this.lastName?.let { lastName ->
                "$firstName $lastName"
            } ?: firstName
        } ?: this.username,
        bio = this.bio,
        skills = this.skills ?: emptyList(), // Handle null case
        interests = this.interests ?: emptyList(), // Handle null case
        university = this.university,
        profileImageUrl = this.profileImageUrl,
        onboardingCompleted = this.onboardingCompleted,
        createdAt = this.createdAt?.toString() ?: "",
        updatedAt = this.updatedAt ?: ""
    )
}

/**
 * Request DTO for creating/updating profile
 * Matches your backend UpdateProfileRequest
 */
data class UpdateProfileRequest(
    val name: String? = null,
    val bio: String? = null,
    val skills: List<String>? = null,
    val interests: List<String>? = null,
    val university: String? = null,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null,
    @SerializedName("onboardingCompleted")
    val onboardingCompleted: Boolean? = null
)

/**
 * Simplified profile for display purposes
 */
data class ProfileSummary(
    val id: String,
    val name: String,
    val university: String?,
    val profileImageUrl: String?,
    val skills: List<String> = emptyList()
)

/**
 * For onboarding completion
 */
data class OnboardingCompletionRequest(
    val onboardingCompleted: Boolean = true
)

/**
 * For profile search results
 */
data class ProfileSearchResponse(
    val profiles: List<UserProfileResponse>,
    val totalCount: Int
)