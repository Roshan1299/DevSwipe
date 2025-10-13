package com.first.devswipe.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "collab_posts")
data class CollabPost(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    var projectTitle: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String,

    @ElementCollection
    @CollectionTable(name = "collab_post_skills", joinColumns = [JoinColumn(name = "collab_post_id")])
    @Column(name = "skill")
    var skillsNeeded: List<String>,

    @Column(nullable = false)
    var timeCommitment: String,

    @Column(nullable = false)
    var teamSize: Int,

    @Column(nullable = false)
    var currentTeamSize: Int = 0,

    @Column(nullable = false)
    var status: String = "active",  // active, filled, completed, cancelled

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    var user: User,

    @CreationTimestamp
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null
)