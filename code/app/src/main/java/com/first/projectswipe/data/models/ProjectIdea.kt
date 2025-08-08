// File: com/first/projectswipe/models/ProjectIdea.kt

package com.first.projectswipe.data.models

data class ProjectIdea(
    val id: String = "",
    val title: String = "",
    val previewDescription: String = "",
    val fullDescription: String = "",
    val createdBy: String = "",
    val tags: List<String> = emptyList(),
    val createdByName: String = "",
    val difficulty: String = "",
    val githubLink: String = "",
    val timeline: String = ""
) {
    override fun toString(): String {
        return "ProjectIdea(title='$title', difficulty='$difficulty', tags=$tags)"
    }
}
