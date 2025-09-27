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
}