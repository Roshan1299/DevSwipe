package com.first.projectswipe.models

data class ProjectIdea(
    val id: String = "",
    val title: String = "",
    val previewDescription: String = "",
    val fullDescription: String = "",
    val createdBy: String = "",  // UID or email of the creator
    val tags: List<String> = emptyList(),
    val createdByName: String = "",
    val difficulty: String = "Beginner",
    val githubLink: String = "",
    val timeline: String = ""
//    val createdAt: Timestamp = Timestamp.now(),
)