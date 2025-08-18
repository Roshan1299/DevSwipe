package com.first.projectswipe.data.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val username: String = "",
    val bio: String = "",
    val skills: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val profileImageUrl: String = "",
    val createdAt: Long = 0L
) {
    // Helper methods
    val displayName: String
        get() = name.ifEmpty { username }

    val initials: String
        get() = if (name.isNotEmpty()) {
            name.split(" ")
                .take(2)
                .map { it.firstOrNull()?.uppercase() ?: "" }
                .joinToString("")
        } else {
            username.take(2).uppercase()
        }
}