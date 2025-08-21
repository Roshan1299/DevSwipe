package com.first.projectswipe.network.dto

import java.util.*

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: UUID,
    val username: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val bio: String? = null,
    val skills: List<String>? = null,
    val interests: List<String>? = null,
    val onboardingCompleted: Boolean? = null,
    val profileImageUrl: String? = null,
    val createdAt: Long? = null
)

data class UpdateUserRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val bio: String? = null,
    val skills: List<String>? = null,
    val interests: List<String>? = null,
    val onboardingCompleted: Boolean? = null,
    val profileImageUrl: String? = null
)

data class UpdateSkillsRequest(
    val skills: List<String>
)

data class UpdateInterestsRequest(
    val interests: List<String>
)