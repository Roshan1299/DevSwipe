package com.first.devswipe.repository

import com.first.devswipe.entity.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectRepository : JpaRepository<Project, UUID> {
    fun findByCreatedBy(createdBy: UUID): List<Project>

    @Query("SELECT p FROM Project p WHERE :tag = ANY(p.tags)")
    fun findByTag(tag: String): List<Project>

    @Query("SELECT p FROM Project p WHERE p.difficulty = :difficulty")
    fun findByDifficulty(difficulty: String): List<Project>
}