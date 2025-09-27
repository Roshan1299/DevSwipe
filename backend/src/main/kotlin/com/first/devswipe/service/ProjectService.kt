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

    fun updateProject(projectId: UUID, request: com.first.devswipe.dto.UpdateProjectRequest): Project {
        val project = projectRepository.findById(projectId).orElseThrow { Exception("Project not found") }

        request.title?.let { project.title = it }
        request.previewDescription?.let { project.previewDescription = it }
        request.fullDescription?.let { project.fullDescription = it }
        request.githubLink?.let { project.githubLink = it }
        request.tags?.let { project.tags = it }
        request.difficulty?.let { project.difficulty = it }

        return projectRepository.save(project)
    }

    fun getProject(projectId: UUID): Project {
        return projectRepository.findById(projectId).orElseThrow { Exception("Project not found") }
    }

    fun getProjectsByUserId(userId: UUID): List<Project> {
        return projectRepository.findByUserId(userId)
    }
}
