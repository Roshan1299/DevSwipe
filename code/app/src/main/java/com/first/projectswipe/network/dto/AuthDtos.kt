// network/dto/AuthDtos.kt
package com.first.projectswipe.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Login request DTO
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Registration request DTO
 */
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val university: String? = null
)


/**
 * Authentication response DTO
 */
data class AuthResponse(
    val token: String,
    val user: UserDto,
    val message: String? = null
)


/**
 * Update skills request DTO
 */
data class UpdateSkillsRequest(
    val skills: List<String>
)

/**
 * Update interests request DTO
 */
data class UpdateInterestsRequest(
    val interests: List<String>
)

data class UserDto(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val university: String? = null,
    val bio: String? = null,
    val skills: List<String>? = null,
    val interests: List<String>? = null,
    val onboardingCompleted: Boolean = false,
    val profileImageUrl: String? = null,
    val createdAt: Long? = null,
    val updatedAt: String? = null
) {
    val fullName: String
        get() = if (!firstName.isNullOrEmpty() || !lastName.isNullOrEmpty()) "${firstName ?: ""} ${lastName ?: ""}".trim() else username
}