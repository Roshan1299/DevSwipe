package com.first.projectswipe.data.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val username: String = "",
    val firstName: String? = null,
    val lastName: String? = null,
    val bio: String = "",
    val skills: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val onboardingCompleted: Boolean = false,
    val profileImageUrl: String = "",
    val createdAt: Long = 0L
) {
    // Helper methods
    val displayName: String
        get() = name.ifEmpty {
            if (firstName != null || lastName != null) {
                "${firstName ?: ""} ${lastName ?: ""}".trim().ifEmpty { username }
            } else {
                username
            }
        }

    val initials: String
        get() = when {
            name.isNotEmpty() -> {
                name.split(" ")
                    .take(2)
                    .map { it.firstOrNull()?.uppercase() ?: "" }
                    .joinToString("")
            }
            firstName != null || lastName != null -> {
                "${firstName?.firstOrNull()?.uppercase() ?: ""}${lastName?.firstOrNull()?.uppercase() ?: ""}"
            }
            else -> {
                username.take(2).uppercase()
            }
        }
}