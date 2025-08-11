package com.first.devswipe.repository

import com.first.devswipe.entity.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectRepository : JpaRepository<Project, UUID> {
    fun findByCreatedBy(createdBy: UUID): List<Project>

    @Query(value = "SELECT * FROM projects WHERE :tag = ANY(tags)", nativeQuery = true)
    fun findByTag(@Param("tag") tag: String): List<Project>

    fun findByDifficulty(difficulty: String): List<Project>
}