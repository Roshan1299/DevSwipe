package com.first.devswipe.repository

import com.first.devswipe.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun findByDisplayUsername(displayUsername: String): User?
    fun existsByEmail(email: String): Boolean
    fun existsByDisplayUsername(displayUsername: String): Boolean
}