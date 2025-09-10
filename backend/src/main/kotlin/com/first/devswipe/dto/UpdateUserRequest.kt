package com.first.devswipe.dto

data class UpdateUserRequest(
    val name: String?,
    val university: String?,
    val bio: String?,
    val profilePictureUrl: String?,
    val skills: List<String>?,
    val interests: List<String>?
)