package com.first.devswipe.service

import com.first.devswipe.dto.ProjectCreateRequest
import com.first.devswipe.entity.Project
import com.first.devswipe.entity.User
import com.first.devswipe.repository.ProjectRepository
import com.first.devswipe.repository.UserProfileRepository
import com.first.devswipe.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository
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

    fun getAllProjects(): List<Project> {
        return projectRepository.findAll()
    }

    fun filterProjects(difficulty: String?, tags: List<String>?): List<Project> {
        return if (difficulty != null && !tags.isNullOrEmpty()) {
            // Both difficulty and tags are specified
            projectRepository.findByDifficultyAndTagsContaining(difficulty, tags)
        } else if (difficulty != null) {
            // Only difficulty is specified
            projectRepository.findByDifficulty(difficulty)
        } else if (!tags.isNullOrEmpty()) {
            // Only tags are specified
            projectRepository.findByTagsContaining(tags)
        } else {
            // No filters, return all projects
            projectRepository.findAll()
        }
    }

    fun deleteProject(projectId: UUID) {
        if (!projectRepository.existsById(projectId)) {
            throw Exception("Project not found")
        }
        projectRepository.deleteById(projectId)
    }

    fun getFeedForUser(user: User, difficulty: String?): List<Project> {
        val profile = userProfileRepository.findByUserId(user.id!!)
        val userSkills = profile?.skills?.map { it.lowercase() } ?: emptyList()
        val userInterests = profile?.interests?.map { it.lowercase() } ?: emptyList()

        val projects = projectRepository.findAll()
            .filter { it.user.id != user.id }
            .let { list -> if (difficulty != null) list.filter { it.difficulty.equals(difficulty, ignoreCase = true) } else list }

        return projects.sortedByDescending { project ->
            val tags = project.tags.map { it.lowercase() }
            tags.count { it in userSkills } + tags.count { it in userInterests }
        }
    }
}
