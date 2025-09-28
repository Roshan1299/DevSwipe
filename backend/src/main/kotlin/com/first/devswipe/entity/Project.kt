package com.first.devswipe.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "projects")
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID = UUID.randomUUID(),

    var title: String,
    var previewDescription: String,
    var fullDescription: String,
    var githubLink: String?,

    @ElementCollection
    var tags: List<String>,

    var difficulty: String,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User
)
