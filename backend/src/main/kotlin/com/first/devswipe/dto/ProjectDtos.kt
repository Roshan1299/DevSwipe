package com.first.devswipe.dto

data class CreateProjectRequest(
    val title: String,
    val fullDescription: String,
    val previewDescription: String,
    val tags: List<String>,
    val difficulty: String? = null,
    val githubLink: String? = null
)

data class UpdateProjectRequest(
    val title: String? = null,
    val fullDescription: String? = null,
    val previewDescription: String? = null,
    val tags: List<String>? = null,
    val difficulty: String? = null,
    val githubLink: String? = null
)