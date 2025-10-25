package com.first.devswipe.controller

import com.first.devswipe.dto.*
import com.first.devswipe.entity.UserProfile
import com.first.devswipe.repository.UserRepository
import com.first.devswipe.repository.UserProfileRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository
) {

    @PatchMapping("/{userId}/skills")
    fun updateUserSkills(
        @PathVariable userId: UUID,
        @RequestBody request: UpdateSkillsRequest,
        authentication: Authentication
    ): ResponseEntity<UserProfileResponse> {
        // Verify that the authenticated user is the same as the one being updated
        val authenticatedUser = userRepository.findByEmail(authentication.name)
            ?: throw RuntimeException("User not found")
        
        if (authenticatedUser.id != userId) {
            return ResponseEntity.status(403).build()
        }

        val profile = userProfileRepository.findByUserId(userId)
            ?: throw RuntimeException("Profile not found")

        val updatedProfile = profile.copy(
            skills = request.skills.toTypedArray(),
            updatedAt = LocalDateTime.now()
        )

        val savedProfile = userProfileRepository.save(updatedProfile)
        return ResponseEntity.ok(savedProfile.toUserProfileResponse())
    }

    @PatchMapping("/{userId}/interests")
    fun updateUserInterests(
        @PathVariable userId: UUID,
        @RequestBody request: UpdateInterestsRequest,
        authentication: Authentication
    ): ResponseEntity<UserProfileResponse> {
        // Verify that the authenticated user is the same as the one being updated
        val authenticatedUser = userRepository.findByEmail(authentication.name)
            ?: throw RuntimeException("User not found")
        
        if (authenticatedUser.id != userId) {
            return ResponseEntity.status(403).build()
        }

        val profile = userProfileRepository.findByUserId(userId)
            ?: throw RuntimeException("Profile not found")

        val updatedProfile = profile.copy(
            interests = request.interests.toTypedArray(),
            updatedAt = LocalDateTime.now()
        )

        val savedProfile = userProfileRepository.save(updatedProfile)
        return ResponseEntity.ok(savedProfile.toUserProfileResponse())
    }

    @PatchMapping("/{userId}/onboarding")
    fun completeOnboarding(
        @PathVariable userId: UUID,
        authentication: Authentication
    ): ResponseEntity<UserProfileResponse> {
        // Verify that the authenticated user is the same as the one being updated
        val authenticatedUser = userRepository.findByEmail(authentication.name)
            ?: throw RuntimeException("User not found")
        
        if (authenticatedUser.id != userId) {
            return ResponseEntity.status(403).build()
        }

        val profile = userProfileRepository.findByUserId(userId)
            ?: throw RuntimeException("Profile not found")

        val updatedProfile = profile.copy(
            onboardingCompleted = true,
            updatedAt = LocalDateTime.now()
        )

        val savedProfile = userProfileRepository.save(updatedProfile)
        return ResponseEntity.ok(savedProfile.toUserProfileResponse())
    }

    @GetMapping("/{userId}")
    fun getUserProfile(@PathVariable userId: UUID): ResponseEntity<UserProfileResponse> {
        val profile = userProfileRepository.findByUserId(userId)
            ?: throw RuntimeException("Profile not found")
        return ResponseEntity.ok(profile.toUserProfileResponse())
    }

    @PutMapping("/{userId}")
    fun updateUser(
        @PathVariable userId: UUID,
        @RequestBody updateRequest: UpdateUserRequest,
        authentication: Authentication
    ): ResponseEntity<UserProfileResponse> {
        // Verify that the authenticated user is the same as the one being updated
        val authenticatedUser = userRepository.findByEmail(authentication.name)
            ?: throw RuntimeException("User not found")
        
        if (authenticatedUser.id != userId) {
            return ResponseEntity.status(403).build()
        }

        val profile = userProfileRepository.findByUserId(userId)
            ?: throw RuntimeException("Profile not found")

        val updatedProfile = profile.copy(
            name = updateRequest.name ?: profile.name,
            bio = updateRequest.bio ?: profile.bio,
            skills = updateRequest.skills?.toTypedArray() ?: profile.skills,
            interests = updateRequest.interests?.toTypedArray() ?: profile.interests,
            university = updateRequest.university ?: profile.university,
            profileImageUrl = updateRequest.profilePictureUrl ?: profile.profileImageUrl,
            onboardingCompleted = updateRequest.onboardingCompleted ?: profile.onboardingCompleted,
            updatedAt = LocalDateTime.now()
        )

        val savedProfile = userProfileRepository.save(updatedProfile)
        return ResponseEntity.ok(savedProfile.toUserProfileResponse())
    }
}