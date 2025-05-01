package com.first.projectswipe.models

data class ProjectIdea(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val createdBy: String = "",  // UID or email of the creator
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)