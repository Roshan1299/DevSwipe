package com.first.devswipe.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "user_profiles")
data class UserProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(columnDefinition = "TEXT")
    val bio: String? = null,

    @Column(columnDefinition = "text[]")
    val interests: Array<String>? = null,

    @Column(nullable = false)
    val name: String,

    @Column(name = "onboarding_completed")
    val onboardingCompleted: Boolean = false,

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    val profileImageUrl: String? = null,

    @Column(columnDefinition = "text[]")
    val skills: Array<String>? = null,

    val university: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    // JPA relationship to User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: User? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as UserProfile
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}