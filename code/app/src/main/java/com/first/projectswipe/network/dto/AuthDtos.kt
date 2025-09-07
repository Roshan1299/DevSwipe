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