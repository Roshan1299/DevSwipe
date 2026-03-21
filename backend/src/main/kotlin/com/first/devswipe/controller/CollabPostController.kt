package com.first.devswipe.controller

import com.first.devswipe.dto.CollabApplicationResponse
import com.first.devswipe.dto.CollaborationCreateRequest
import com.first.devswipe.dto.CollaborationResponse
import com.first.devswipe.dto.UpdateCollaborationRequest
import com.first.devswipe.dto.UserDto
import com.first.devswipe.entity.User
import com.first.devswipe.service.CollabApplicationService
import com.first.devswipe.service.CollabPostService
import com.first.devswipe.repository.UserProfileRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/collaborations")
class CollabPostController(
    private val collabPostService: CollabPostService,
    private val userProfileRepository: UserProfileRepository,
    private val collabApplicationService: CollabApplicationService
) {

    @PostMapping
    fun createCollabPost(
        @AuthenticationPrincipal user: User,
        @RequestBody request: CollaborationCreateRequest
    ): ResponseEntity<CollaborationResponse> {
        val collabPost = collabPostService.createCollabPost(user.id!!, request)
        val userProfile = userProfileRepository.findByUserId(user.id!!)
        val userDto = UserDto(
            id = user.id!!,
            username = user.displayName,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            university = userProfile?.university
        )
        val response = CollaborationResponse(
            id = collabPost.id!!,
            projectTitle = collabPost.projectTitle,
            description = collabPost.description,
            skillsNeeded = collabPost.skillsNeeded,
            timeCommitment = collabPost.timeCommitment,
            teamSize = collabPost.teamSize,
            currentTeamSize = collabPost.currentTeamSize,
            status = collabPost.status,
            createdBy = userDto,
            createdAt = collabPost.createdAt?.toEpochSecond(java.time.ZoneOffset.UTC) ?: 0,
            updatedAt = collabPost.updatedAt?.toEpochSecond(java.time.ZoneOffset.UTC) ?: 0
        )
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{postId}")
    fun updateCollabPost(
        @PathVariable postId: UUID,
        @RequestBody request: UpdateCollaborationRequest
    ): ResponseEntity<CollaborationResponse> {
        val collabPost = collabPostService.updateCollabPost(postId, request)
        val userProfile = userProfileRepository.findByUserId(collabPost.user.id!!)
        val userDto = UserDto(
            id = collabPost.user.id!!,
            username = collabPost.user.displayName,
            email = collabPost.user.email,
            firstName = collabPost.user.firstName,
            lastName = collabPost.user.lastName,
            university = userProfile?.university
        )
        val response = CollaborationResponse(
            id = collabPost.id!!,
            projectTitle = collabPost.projectTitle,
            description = collabPost.description,
            skillsNeeded = collabPost.skillsNeeded,
            timeCommitment = collabPost.timeCommitment,
            teamSize = collabPost.teamSize,
            currentTeamSize = collabPost.currentTeamSize,
            status = collabPost.status,
            createdBy = userDto,
            createdAt = collabPost.createdAt?.toEpochSecond(java.time.ZoneOffset.UTC) ?: 0,
            updatedAt = collabPost.updatedAt?.toEpochSecond(java.time.ZoneOffset.UTC) ?: 0
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{postId}")
    fun getCollabPost(@PathVariable postId: UUID): ResponseEntity<CollaborationResponse> {
        val collabPost = collabPostService.getCollabPost(postId)
        val userProfile = userProfileRepository.findByUserId(collabPost.user.id!!)
        val userDto = UserDto(
            id = collabPost.user.id!!,
            username = collabPost.user.displayName,
            email = collabPost.user.email,
            firstName = collabPost.user.firstName,
            lastName = collabPost.user.lastName,
            university = userProfile?.university
        )
        val response = CollaborationResponse(
            id = collabPost.id!!,
            projectTitle = collabPost.projectTitle,
            description = collabPost.description,
            skillsNeeded = collabPost.skillsNeeded,
            timeCommitment = collabPost.timeCommitment,
            teamSize = collabPost.teamSize,
            currentTeamSize = collabPost.currentTeamSize,
            status = collabPost.status,
            createdBy = userDto,
            createdAt = collabPost.createdAt?.toEpochSecond(java.time.ZoneOffset.UTC) ?: 0,
            updatedAt = collabPost.updatedAt?.toEpochSecond(java.time.ZoneOffset.UTC) ?: 0
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/user/{userId}")
    fun getUserCollabPosts(@PathVariable userId: UUID): ResponseEntity<List<CollaborationResponse>> {
        val collabPosts = collabPostService.getCollabPostsByUserId(userId)
        val responses = collabPosts.map { collabPost ->
            val userProfile = userProfileRepository.findByUserId(collabPost.user.id!!)
            val userDto = UserDto(
                id = collabPost.user.id!!,
                username = collabPost.user.displayName,
                email = collabPost.user.email,
                firstName = collabPost.user.firstName,
                lastName = collabPost.user.lastName,
                university = userProfile?.university
            )
            CollaborationResponse(
                id = collabPost.id!!,
                projectTitle = collabPost.projectTitle,
                description = collabPost.description,
                skillsNeeded = collabPost.skillsNeeded,
                timeCommitment = collabPost.timeCommitment,
                teamSize = collabPost.teamSize,
                currentTeamSize = collabPost.currentTeamSize,
                status = collabPost.status,
                createdBy = userDto,
                createdAt = collabPost.createdAt?.toEpochSecond(java.time.ZoneOffset.UTC) ?: 0,
                updatedAt = collabPost.updatedAt?.toEpochSecond(java.time.ZoneOffset.UTC) ?: 0
            )
        }
        return ResponseEntity.ok(responses)
    }

    @GetMapping("/my-collaborations")
    fun getCurrentUserCollabPosts(@AuthenticationPrincipal user: User): ResponseEntity<List<CollaborationResponse>> {
        val collabPosts = collabPostService.getCollabPostsByUserId(user.id!!)
        val responses = collabPosts.map { collabPost ->
            val userProfile = userProfileRepository.findByUserId(collabPost.user.id!!)
            val userDto = UserDto(
                id = collabPost.user.id!!,
                username = collabPost.user.displayName,
                email = collabPost.user.email,
                firstName = collabPost.user.firstName,
                lastName = collabPost.user.lastName,
                university = userProfile?.university
            )
            CollaborationResponse(
                id = collabPost.id!!,
                projectTitle = collabPost.projectTitle,
                description = collabPost.description,
                skillsNeeded = collabPost.skillsNeeded,
                timeCommitment = collabPost.timeCommitment,
                teamSize = collabPost.teamSize,
                currentTeamSize = collabPost.currentTeamSize,
                status = collabPost.status,
                createdBy = userDto,
                createdAt = collabPost.createdAt?.toEpochSecond(java.time.ZoneOffset.UTC) ?: 0,
                updatedAt = collabPost.updatedAt?.toEpochSecond(java.time.ZoneOffset.UTC) ?: 0
            )
        }
        return ResponseEntity.ok(responses)
    }

    @GetMapping
    fun getAllCollabPosts(): ResponseEntity<List<CollaborationResponse>> {
        val collabPosts = collabPostService.getAllCollabPosts()
        val responses = collabPosts.map { collabPost ->
            val userProfile = userProfileRepository.findByUserId(collabPost.user.id!!)
            val userDto = UserDto(
                id = collabPost.user.id!!,
                username = collabPost.user.displayName,
                email = collabPost.user.email,
                firstName = collabPost.user.firstName,
                lastName = collabPost.user.lastName,
                university = userProfile?.university
            )
            CollaborationResponse(
                id = collabPost.id!!,
                projectTitle = collabPost.projectTitle,
                description = collabPost.description,
                skillsNeeded = collabPost.skillsNeeded,
                timeCommitment = collabPost.timeCommitment,
                teamSize = collabPost.teamSize,
                currentTeamSize = collabPost.currentTeamSize,
                status = collabPost.status,
                createdBy = userDto,
                createdAt = collabPost.createdAt?.toEpochSecond(java.time.ZoneOffset.UTC) ?: 0,
                updatedAt = collabPost.updatedAt?.toEpochSecond(java.time.ZoneOffset.UTC) ?: 0
            )
        }
        return ResponseEntity.ok(responses)
    }

    @DeleteMapping("/{postId}")
    fun deleteCollabPost(
        @PathVariable postId: UUID,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<Map<String, String>> {
        try {
            val collabPost = collabPostService.getCollabPost(postId)
            
            // Check if the user owns this collaboration post
            if (collabPost.user.id != user.id) {
                return ResponseEntity.status(403).body(mapOf("error" to "You can only delete your own collaboration posts"))
            }
            
            collabPostService.deleteCollabPost(postId)
            return ResponseEntity.ok(mapOf("message" to "Collaboration post deleted successfully"))
        } catch (e: Exception) {
            return ResponseEntity.status(404).body(mapOf("error" to "Collaboration post not found: ${e.message}"))
        }
    }

    // Apply to a collab post (swipe right)
    @PostMapping("/{postId}/apply")
    fun applyToCollabPost(
        @PathVariable postId: UUID,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<Any> {
        return try {
            val application = collabApplicationService.apply(user.id!!, postId)
            ResponseEntity.ok(mapOf(
                "message" to "Applied successfully",
                "applicationId" to application.id.toString()
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(404).body(mapOf("error" to e.message))
        }
    }

    // Get applicants for a post (owner only)
    @GetMapping("/{postId}/applicants")
    fun getApplicants(
        @PathVariable postId: UUID,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<Any> {
        return try {
            val applications = collabApplicationService.getApplicantsForPost(postId, user.id!!)
            val responses = applications.map { app ->
                val userProfile = userProfileRepository.findByUserId(app.applicant.id!!)
                CollabApplicationResponse(
                    id = app.id!!,
                    collabPostId = app.collabPost.id!!,
                    collabPostTitle = app.collabPost.projectTitle,
                    applicant = UserDto(
                        id = app.applicant.id!!,
                        username = app.applicant.displayName,
                        email = app.applicant.email,
                        firstName = app.applicant.firstName,
                        lastName = app.applicant.lastName,
                        university = userProfile?.university,
                        profileImageUrl = app.applicant.profilePictureUrl
                    ),
                    applicantSkills = userProfile?.skills?.toList() ?: emptyList(),
                    status = app.status,
                    createdAt = app.createdAt?.toEpochSecond(java.time.ZoneOffset.UTC) ?: 0
                )
            }
            ResponseEntity.ok(responses)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(403).body(mapOf("error" to e.message))
        }
    }

    // Accept an applicant
    @PutMapping("/{postId}/applicants/{applicantId}/accept")
    fun acceptApplicant(
        @PathVariable postId: UUID,
        @PathVariable applicantId: UUID,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<Any> {
        return try {
            val application = collabApplicationService.acceptApplication(postId, applicantId, user.id!!)
            ResponseEntity.ok(mapOf("message" to "Applicant accepted", "status" to application.status))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    // Decline an applicant
    @PutMapping("/{postId}/applicants/{applicantId}/decline")
    fun declineApplicant(
        @PathVariable postId: UUID,
        @PathVariable applicantId: UUID,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<Any> {
        return try {
            val application = collabApplicationService.declineApplication(postId, applicantId, user.id!!)
            ResponseEntity.ok(mapOf("message" to "Applicant declined", "status" to application.status))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    // Get current user's applications
    @GetMapping("/my-applications")
    fun getMyApplications(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<List<CollabApplicationResponse>> {
        val applications = collabApplicationService.getMyApplications(user.id!!)
        val responses = applications.map { app ->
            val userProfile = userProfileRepository.findByUserId(app.applicant.id!!)
            CollabApplicationResponse(
                id = app.id!!,
                collabPostId = app.collabPost.id!!,
                collabPostTitle = app.collabPost.projectTitle,
                applicant = UserDto(
                    id = app.applicant.id!!,
                    username = app.applicant.displayName,
                    email = app.applicant.email,
                    firstName = app.applicant.firstName,
                    lastName = app.applicant.lastName,
                    university = userProfile?.university,
                    profileImageUrl = app.applicant.profilePictureUrl
                ),
                applicantSkills = userProfile?.skills?.toList() ?: emptyList(),
                status = app.status,
                createdAt = app.createdAt?.toEpochSecond(java.time.ZoneOffset.UTC) ?: 0
            )
        }
        return ResponseEntity.ok(responses)
    }
}