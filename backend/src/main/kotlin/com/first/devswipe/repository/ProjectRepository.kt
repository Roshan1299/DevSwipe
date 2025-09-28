package com.first.devswipe.repository

import com.first.devswipe.entity.Project
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProjectRepository : JpaRepository<Project, UUID> {
    fun findByUserId(userId: UUID): List<Project>
}
