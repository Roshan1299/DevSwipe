package com.first.devswipe.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "collab_applications",
    uniqueConstraints = [UniqueConstraint(columnNames = ["collab_post_id", "applicant_id"])]
)
data class CollabApplication(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collab_post_id", nullable = false)
    val collabPost: CollabPost,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    val applicant: User,

    @Column(nullable = false)
    var status: String = "PENDING", // PENDING, ACCEPTED, DECLINED

    @CreationTimestamp
    val createdAt: LocalDateTime? = null
)
