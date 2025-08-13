package com.first.devswipe.repository

import com.first.devswipe.entity.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectRepository : JpaRepository<Project, UUID> {
    fun findByCreatedByOrderByCreatedAtDesc(createdBy: UUID): List<Project>

    fun findByDifficultyOrderByCreatedAtDesc(difficulty: String): List<Project>

    @Query("SELECT p FROM Project p WHERE p.title ILIKE %:query% OR p.fullDescription ILIKE %:query% OR p.previewDescription ILIKE %:query%")
    fun searchProjects(@Param("query") query: String): List<Project>

    @Query(value = "SELECT * FROM projects WHERE :tag = ANY(tags)", nativeQuery = true)
    fun findByTagsContaining(@Param("tag") tag: String): List<Project>

    @Query("SELECT p FROM Project p WHERE p.createdBy = :userId")
    fun findByUserId(@Param("userId") userId: UUID): List<Project>
}