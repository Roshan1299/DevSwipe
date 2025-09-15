package com.first.devswipe.service

import com.first.devswipe.dto.ProjectCreateRequest
import com.first.devswipe.entity.Project
import com.first.devswipe.repository.ProjectRepository
import com.first.devswipe.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) {

    fun createProject(userId: UUID, request: ProjectCreateRequest): Project {
        val user = userRepository.findById(userId).orElseThrow { Exception("User not found") }
        val project = Project(
            title = request.title,
            previewDescription = request.previewDescription,
            fullDescription = request.fullDescription,
            githubLink = request.githubLink,
            tags = request.tags,
            difficulty = request.difficulty,
            user = user
        )
        return projectRepository.save(project)
    }
}
