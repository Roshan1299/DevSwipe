package com.first.devswipe.repository

import com.first.devswipe.entity.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserProfileRepository : JpaRepository<UserProfile, UUID> {
    fun findByUserId(userId: UUID): UserProfile?

    @Query(value = "SELECT * FROM user_profiles WHERE :skill = ANY(skills)", nativeQuery = true)
    fun findBySkill(@Param("skill") skill: String): List<UserProfile>

    @Query(value = "SELECT * FROM user_profiles WHERE :interest = ANY(interests)", nativeQuery = true)
    fun findByInterest(@Param("interest") interest: String): List<UserProfile>
}