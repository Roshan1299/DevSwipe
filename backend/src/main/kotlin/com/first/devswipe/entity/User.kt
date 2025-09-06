package com.first.devswipe.entity

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "username", unique = true, nullable = false)
    val displayName: String,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(name = "password_hash", nullable = false)
    private val passwordHash: String,

    @Column(name = "first_name")
    val firstName: String? = null,

    @Column(name = "last_name")
    val lastName: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "profile_picture_url")
    val profilePictureUrl: String? = null,

    @Column(name = "bio")
    val bio: String? = null,

    @Column(name = "university")
    val university: String? = null
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getPassword(): String = passwordHash
    override fun getUsername(): String = email // Use email as the username for Spring Security
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}
