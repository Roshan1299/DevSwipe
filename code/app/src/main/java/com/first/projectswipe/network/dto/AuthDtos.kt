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
    val firstName: String?,
    val lastName: String?
)
