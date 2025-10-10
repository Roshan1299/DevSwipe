package com.first.devswipe.controller

import com.first.devswipe.dto.ProjectCreateRequest
import com.first.devswipe.dto.ProjectResponse
import com.first.devswipe.dto.UserDto
import com.first.devswipe.entity.User
import com.first.devswipe.service.ProjectService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import com.first.devswipe.repository.UserProfileRepository
import java.util.UUID

@RestController
@RequestMapping("/api/projects")
class ProjectController(
    private val projectService: ProjectService,
    private val userProfileRepository: UserProfileRepository
) {

    @PostMapping
    fun createProject(
        @AuthenticationPrincipal user: User,
        @RequestBody request: ProjectCreateRequest
    ): ResponseEntity<ProjectResponse> {
        val project = projectService.createProject(user.id!!, request)
        val userProfile = userProfileRepository.findByUserId(user.id!!)
        val userDto = UserDto(
            id = user.id!!,
            username = user.displayName,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            university = userProfile?.university
        )
        val response = ProjectResponse(
            id = project.id,
            title = project.title,
            previewDescription = project.previewDescription,
            fullDescription = project.fullDescription,
            githubLink = project.githubLink,
            tags = project.tags,
            difficulty = project.difficulty,
            createdBy = userDto
        )
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{projectId}")
    fun updateProject(
        @PathVariable projectId: UUID,
        @RequestBody request: com.first.devswipe.dto.UpdateProjectRequest
    ): ResponseEntity<ProjectResponse> {
        val project = projectService.updateProject(projectId, request)
        val userProfile = userProfileRepository.findByUserId(project.user.id!!)
        val userDto = UserDto(
            id = project.user.id!!,
            username = project.user.displayName,
            email = project.user.email,
            firstName = project.user.firstName,
            lastName = project.user.lastName,
            university = userProfile?.university
        )
        val response = ProjectResponse(
            id = project.id,
            title = project.title,
            previewDescription = project.previewDescription,
            fullDescription = project.fullDescription,
            githubLink = project.githubLink,
            tags = project.tags,
            difficulty = project.difficulty,
            createdBy = userDto
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID): ResponseEntity<ProjectResponse> {
        val project = projectService.getProject(projectId)
        val userProfile = userProfileRepository.findByUserId(project.user.id!!)
        val userDto = UserDto(
            id = project.user.id!!,
            username = project.user.displayName,
            email = project.user.email,
            firstName = project.user.firstName,
            lastName = project.user.lastName,
            university = userProfile?.university
        )
        val response = ProjectResponse(
            id = project.id,
            title = project.title,
            previewDescription = project.previewDescription,
            fullDescription = project.fullDescription,
            githubLink = project.githubLink,
            tags = project.tags,
            difficulty = project.difficulty,
            createdBy = userDto
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/user/{userId}")
    fun getUserProjects(@PathVariable userId: UUID): ResponseEntity<List<ProjectResponse>> {
        val projects = projectService.getProjectsByUserId(userId)
        val responses = projects.map { project ->
            val userProfile = userProfileRepository.findByUserId(project.user.id!!)
            val userDto = UserDto(
                id = project.user.id!!,
                username = project.user.displayName,
                email = project.user.email,
                firstName = project.user.firstName,
                lastName = project.user.lastName,
                university = userProfile?.university
            )
            ProjectResponse(
                id = project.id,
                title = project.title,
                previewDescription = project.previewDescription,
                fullDescription = project.fullDescription,
                githubLink = project.githubLink,
                tags = project.tags,
                difficulty = project.difficulty,
                createdBy = userDto
            )
        }
        return ResponseEntity.ok(responses)
    }

    @GetMapping("/my-projects")
    fun getCurrentUserProjects(@AuthenticationPrincipal user: User): ResponseEntity<List<ProjectResponse>> {
        val projects = projectService.getProjectsByUserId(user.id!!)
        val responses = projects.map { project ->
            val userProfile = userProfileRepository.findByUserId(project.user.id!!)
            val userDto = UserDto(
                id = project.user.id!!,
                username = project.user.displayName,
                email = project.user.email,
                firstName = project.user.firstName,
                lastName = project.user.lastName,
                university = userProfile?.university
            )
            ProjectResponse(
                id = project.id,
                title = project.title,
                previewDescription = project.previewDescription,
                fullDescription = project.fullDescription,
                githubLink = project.githubLink,
                tags = project.tags,
                difficulty = project.difficulty,
                createdBy = userDto
            )
        }
        return ResponseEntity.ok(responses)
    }

    @GetMapping
    fun getAllProjects(): ResponseEntity<List<ProjectResponse>> {
        val projects = projectService.getAllProjects()
        val responses = projects.map { project ->
            val userProfile = userProfileRepository.findByUserId(project.user.id!!)
            val userDto = UserDto(
                id = project.user.id!!,
                username = project.user.displayName,
                email = project.user.email,
                firstName = project.user.firstName,
                lastName = project.user.lastName,
                university = userProfile?.university
            )
            ProjectResponse(
                id = project.id,
                title = project.title,
                previewDescription = project.previewDescription,
                fullDescription = project.fullDescription,
                githubLink = project.githubLink,
                tags = project.tags,
                difficulty = project.difficulty,
                createdBy = userDto
            )
        }
        return ResponseEntity.ok(responses)
    }

    @GetMapping("/filter")
    fun filterProjects(
        @RequestParam(required = false) difficulty: String?,
        @RequestParam(required = false) tags: List<String>?
    ): ResponseEntity<List<ProjectResponse>> {
        val projects = projectService.filterProjects(difficulty, tags)
        val responses = projects.map { project ->
            val userProfile = userProfileRepository.findByUserId(project.user.id!!)
            val userDto = UserDto(
                id = project.user.id!!,
                username = project.user.displayName,
                email = project.user.email,
                firstName = project.user.firstName,
                lastName = project.user.lastName,
                university = userProfile?.university
            )
            ProjectResponse(
                id = project.id,
                title = project.title,
                previewDescription = project.previewDescription,
                fullDescription = project.fullDescription,
                githubLink = project.githubLink,
                tags = project.tags,
                difficulty = project.difficulty,
                createdBy = userDto
            )
        }
        return ResponseEntity.ok(responses)
    }

    @DeleteMapping("/{projectId}")
    fun deleteProject(
        @PathVariable projectId: UUID,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<Map<String, String>> {
        try {
            val project = projectService.getProject(projectId)
            
            // Check if the user owns this project
            if (project.user.id != user.id) {
                return ResponseEntity.status(403).body(mapOf("error" to "You can only delete your own projects"))
            }
            
            projectService.deleteProject(projectId)
            return ResponseEntity.ok(mapOf("message" to "Project deleted successfully"))
        } catch (e: Exception) {
            return ResponseEntity.status(404).body(mapOf("error" to "Project not found: ${e.message}"))
        }
    }
}