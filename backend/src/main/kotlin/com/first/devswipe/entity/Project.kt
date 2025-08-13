package com.first.devswipe.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "projects")
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "created_by", nullable = false)
    val createdBy: UUID,

    val difficulty: String? = null,

    @Column(name = "full_description", nullable = false, columnDefinition = "TEXT")
    val fullDescription: String,

    @Column(name = "github_link")
    val githubLink: String? = null,

    @Column(name = "preview_description", nullable = false, columnDefinition = "TEXT")
    val previewDescription: String,

    @Column(columnDefinition = "text[]")
    val tags: Array<String>,

    @Column(nullable = false)
    val title: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    // JPA relationship to User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    val creator: User? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Project
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}