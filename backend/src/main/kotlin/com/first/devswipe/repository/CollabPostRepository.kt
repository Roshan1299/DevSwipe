package com.first.devswipe.repository

import com.first.devswipe.entity.CollabPost
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CollabPostRepository : JpaRepository<CollabPost, UUID> {
    fun findByUserId(userId: UUID): List<CollabPost>
}