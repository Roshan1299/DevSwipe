package com.first.devswipe.dto

data class UpdateProfileRequest(
    val name: String? = null,
    val bio: String? = null,
    val skills: List<String>? = null,
    val interests: List<String>? = null,
    val university: String? = null,
    val profileImageUrl: String? = null,
    val onboardingCompleted: Boolean? = null
)