package com.first.projectswipe.presentation.ui.auth

import com.first.projectswipe.network.dto.UserDto

sealed class AuthResult {
    data class Success(val user: UserDto) : AuthResult()
    data class Error(val message: String) : AuthResult()
}