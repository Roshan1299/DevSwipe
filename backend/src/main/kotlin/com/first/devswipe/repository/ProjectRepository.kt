package com.first.devswipe.repository

import com.first.devswipe.entity.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ProjectRepository : JpaRepository<Project, UUID> {
    fun findByUserId(userId: UUID): List<Project>
    
    fun findByDifficulty(difficulty: String): List<Project>
    
    @Query("SELECT p FROM Project p WHERE :tag MEMBER OF p.tags")
    fun findByTag(@Param("tag") tag: String): List<Project>
    
    @Query("SELECT DISTINCT p FROM Project p JOIN p.tags t WHERE t IN :tags")
    fun findByTagsContaining(@Param("tags") tags: List<String>): List<Project>
    
    @Query("SELECT DISTINCT p FROM Project p JOIN p.tags t WHERE p.difficulty = :difficulty AND t IN :tags")
    fun findByDifficultyAndTagsContaining(@Param("difficulty") difficulty: String, @Param("tags") tags: List<String>): List<Project>
}
