package com.first.devswipe.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "conversations")
data class Conversation(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    val user1: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    val user2: User,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "last_message_content")
    var lastMessageContent: String? = null,

    @Column(name = "last_message_time")
    var lastMessageTime: LocalDateTime? = null
) {
    // Composite unique constraint to ensure only one conversation exists between two users
    @PrePersist
    fun beforeCreate() {
        updatedAt = LocalDateTime.now()
    }

    @PreUpdate
    fun beforeUpdate() {
        updatedAt = LocalDateTime.now()
    }
}