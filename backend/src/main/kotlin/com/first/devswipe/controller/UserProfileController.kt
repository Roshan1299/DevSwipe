package com.first.devswipe.controller

import com.first.devswipe.dto.UpdateProfileRequest
import com.first.devswipe.dto.UserProfileResponse
import com.first.devswipe.dto.toUserProfileResponse
import com.first.devswipe.entity.UserProfile
import com.first.devswipe.repository.UserRepository
import com.first.devswipe.repository.UserProfileRepository
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/profile")
class UserProfileController(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository
) {

    @GetMapping
    fun getCurrentUserProfile(authentication: Authentication): ResponseEntity<UserProfileResponse> {
        val user = userRepository.findByEmail(authentication.name)
            ?: throw RuntimeException("User not found")

        var profile = userProfileRepository.findByUserId(user.id!!)
            ?: throw RuntimeException("Profile not found. Please create your profile first.")

        if (profile == null) {
            profile = UserProfile(
                userId = user.id!!,
                name = "${user.firstName ?: ""} ${user.lastName ?: ""}".trim()
                    .ifEmpty { user.username ?: "User" },
                bio = null,
                skills = emptyArray(),
                interests = emptyArray(),
                university = null,
                profileImageUrl = null,
                onboardingCompleted = false
            )
            profile = userProfileRepository.save(profile)
        }

        return ResponseEntity.ok(profile.toUserProfileResponse())
    }

    @PostMapping
    fun createOrUpdateProfile(
        @Valid @RequestBody request: UpdateProfileRequest,
        authentication: Authentication
    ): ResponseEntity<UserProfileResponse> {
        val user = userRepository.findByEmail(authentication.name)
            ?: throw RuntimeException("User not found")

        val existingProfile = userProfileRepository.findByUserId(user.id!!)

        val profile = existingProfile?.copy(
            name = request.name ?: existingProfile.name,
            bio = request.bio ?: existingProfile.bio,
            skills = request.skills?.toTypedArray() ?: existingProfile.skills,
            interests = request.interests?.toTypedArray() ?: existingProfile.interests,
            university = request.university ?: existingProfile.university,
            profileImageUrl = request.profileImageUrl ?: existingProfile.profileImageUrl,
            onboardingCompleted = request.onboardingCompleted ?: existingProfile.onboardingCompleted,
            updatedAt = LocalDateTime.now()
        ) ?: UserProfile(
            userId = user.id!!,
            name = request.name ?: "User",
            bio = request.bio,
            skills = request.skills?.toTypedArray(),
            interests = request.interests?.toTypedArray(),
            university = request.university,
            profileImageUrl = request.profileImageUrl,
            onboardingCompleted = request.onboardingCompleted ?: false
        )

        val savedProfile = userProfileRepository.save(profile)
        return ResponseEntity.ok(savedProfile.toUserProfileResponse())
    }

    @GetMapping("/{userId}")
    fun getUserProfile(@PathVariable userId: UUID): ResponseEntity<UserProfileResponse> {
        val profile = userProfileRepository.findByUserId(userId)
            ?: throw RuntimeException("Profile not found")
        return ResponseEntity.ok(profile.toUserProfileResponse())
    }

    @PutMapping("/complete-onboarding")
    fun completeOnboarding(authentication: Authentication): ResponseEntity<UserProfileResponse> {
        val user = userRepository.findByEmail(authentication.name)
            ?: throw RuntimeException("User not found")

        val profile = userProfileRepository.findByUserId(user.id!!)
            ?: throw RuntimeException("Profile not found")

        val updatedProfile = profile.copy(
            onboardingCompleted = true,
            updatedAt = LocalDateTime.now()
        )

        val savedProfile = userProfileRepository.save(updatedProfile)
        return ResponseEntity.ok(savedProfile.toUserProfileResponse())
    }

    @GetMapping("/search/skills/{skill}")
    fun getUsersBySkill(@PathVariable skill: String): ResponseEntity<List<UserProfileResponse>> {
        val profiles = userProfileRepository.findBySkillsContaining(skill)
        return ResponseEntity.ok(profiles.map { it.toUserProfileResponse() })
    }

    @GetMapping("/search/interests/{interest}")
    fun getUsersByInterest(@PathVariable interest: String): ResponseEntity<List<UserProfileResponse>> {
        val profiles = userProfileRepository.findByInterestsContaining(interest)
        return ResponseEntity.ok(profiles.map { it.toUserProfileResponse() })
    }
}