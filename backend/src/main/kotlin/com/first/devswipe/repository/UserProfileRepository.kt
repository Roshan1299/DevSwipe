package com.first.devswipe.repository

import com.first.devswipe.entity.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserProfileRepository : JpaRepository<UserProfile, UUID> {
    fun findByUserId(userId: UUID): UserProfile?

    @Query("SELECT up FROM UserProfile up WHERE :skill = ANY(up.skills)")
    fun findBySkill(skill: String): List<UserProfile>

    @Query("SELECT up FROM UserProfile up WHERE :interest = ANY(up.interests)")
    fun findByInterest(interest: String): List<UserProfile>
}