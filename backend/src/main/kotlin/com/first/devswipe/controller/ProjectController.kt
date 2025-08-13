package com.first.devswipe.controller

import com.first.devswipe.entity.Project
import com.first.devswipe.repository.ProjectRepository
import com.first.devswipe.repository.UserRepository
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.first.devswipe.dto.CreateProjectRequest
import org.springframework.security.core.Authentication
import com.first.devswipe.dto.UpdateProjectRequest
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/projects")
class ProjectController(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) {

    @GetMapping
    fun getAllProjects(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<Project>> {
        val pageable = PageRequest.of(page, size)
        val projects = projectRepository.findAll(pageable)
        return ResponseEntity.ok(projects)
    }

    @GetMapping("/{id}")
    fun getProjectById(@PathVariable id: UUID): ResponseEntity<Project> {
        val project = projectRepository.findById(id)
            .orElseThrow { RuntimeException("Project not found") }
        return ResponseEntity.ok(project)
    }

    @PostMapping
    fun createProject(
        @Valid @RequestBody request: CreateProjectRequest,
        authentication: Authentication
    ): ResponseEntity<Project> {
        val user = userRepository.findByEmail(authentication.name)
            ?: throw RuntimeException("User not found")

        val project = Project(
            title = request.title,
            fullDescription = request.fullDescription,
            previewDescription = request.previewDescription,
            tags = request.tags.toTypedArray(),
            difficulty = request.difficulty,
            githubLink = request.githubLink,
            createdBy = user.id!!
        )

        val savedProject = projectRepository.save(project)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProject)
    }

    @PutMapping("/{id}")
    fun updateProject(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateProjectRequest,
        authentication: Authentication
    ): ResponseEntity<Project> {
        val project = projectRepository.findById(id)
            .orElseThrow { RuntimeException("Project not found") }

        val user = userRepository.findByEmail(authentication.name)
            ?: throw RuntimeException("User not found")

        // Check if user owns this project
        if (project.createdBy != user.id) {
            throw RuntimeException("Unauthorized to update this project")
        }

        val updatedProject = project.copy(
            title = request.title ?: project.title,
            fullDescription = request.fullDescription ?: project.fullDescription,
            previewDescription = request.previewDescription ?: project.previewDescription,
            tags = request.tags?.toTypedArray() ?: project.tags,
            difficulty = request.difficulty ?: project.difficulty,
            githubLink = request.githubLink ?: project.githubLink,
            updatedAt = LocalDateTime.now()
        )

        val savedProject = projectRepository.save(updatedProject)
        return ResponseEntity.ok(savedProject)
    }

    @DeleteMapping("/{id}")
    fun deleteProject(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<Unit> {
        val project = projectRepository.findById(id)
            .orElseThrow { RuntimeException("Project not found") }

        val user = userRepository.findByEmail(authentication.name)
            ?: throw RuntimeException("User not found")

        if (project.createdBy != user.id) {
            throw RuntimeException("Unauthorized to delete this project")
        }

        projectRepository.delete(project)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/my")
    fun getMyProjects(authentication: Authentication): ResponseEntity<List<Project>> {
        val user = userRepository.findByEmail(authentication.name)
            ?: throw RuntimeException("User not found")

        val projects = projectRepository.findByCreatedByOrderByCreatedAtDesc(user.id!!)
        return ResponseEntity.ok(projects)
    }

    @GetMapping("/search")
    fun searchProjects(@RequestParam query: String): ResponseEntity<List<Project>> {
        val projects = projectRepository.searchProjects(query)
        return ResponseEntity.ok(projects)
    }

    @GetMapping("/by-tag/{tag}")
    fun getProjectsByTag(@PathVariable tag: String): ResponseEntity<List<Project>> {
        val projects = projectRepository.findByTagsContaining(tag)
        return ResponseEntity.ok(projects)
    }

    @GetMapping("/by-difficulty/{difficulty}")
    fun getProjectsByDifficulty(@PathVariable difficulty: String): ResponseEntity<List<Project>> {
        val projects = projectRepository.findByDifficultyOrderByCreatedAtDesc(difficulty)
        return ResponseEntity.ok(projects)
    }
}
