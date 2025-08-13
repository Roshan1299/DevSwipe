package com.first.devswipe.controller

import com.first.devswipe.entity.UserProfile
import com.first.devswipe.repository.UserRepository
import com.first.devswipe.repository.UserProfileRepository
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import com.first.devswipe.dto.UpdateProfileRequest
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
    fun getCurrentUserProfile(authentication: Authentication): ResponseEntity<UserProfile> {
        val user = userRepository.findByEmail(authentication.name)
            ?: throw RuntimeException("User not found")

        val profile = userProfileRepository.findByUserId(user.id!!)
            ?: throw RuntimeException("Profile not found. Please create your profile first.")

        return ResponseEntity.ok(profile)
    }

    @PostMapping
    fun createOrUpdateProfile(
        @Valid @RequestBody request: UpdateProfileRequest,
        authentication: Authentication
    ): ResponseEntity<UserProfile> {
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
        return ResponseEntity.ok(savedProfile)
    }

    @GetMapping("/{userId}")
    fun getUserProfile(@PathVariable userId: UUID): ResponseEntity<UserProfile> {
        val profile = userProfileRepository.findByUserId(userId)
            ?: throw RuntimeException("Profile not found")
        return ResponseEntity.ok(profile)
    }

    @PutMapping("/complete-onboarding")
    fun completeOnboarding(authentication: Authentication): ResponseEntity<UserProfile> {
        val user = userRepository.findByEmail(authentication.name)
            ?: throw RuntimeException("User not found")

        val profile = userProfileRepository.findByUserId(user.id!!)
            ?: throw RuntimeException("Profile not found")

        val updatedProfile = profile.copy(
            onboardingCompleted = true,
            updatedAt = LocalDateTime.now()
        )

        val savedProfile = userProfileRepository.save(updatedProfile)
        return ResponseEntity.ok(savedProfile)
    }

    @GetMapping("/search/skills/{skill}")
    fun getUsersBySkill(@PathVariable skill: String): ResponseEntity<List<UserProfile>> {
        val profiles = userProfileRepository.findBySkillsContaining(skill)
        return ResponseEntity.ok(profiles)
    }

    @GetMapping("/search/interests/{interest}")
    fun getUsersByInterest(@PathVariable interest: String): ResponseEntity<List<UserProfile>> {
        val profiles = userProfileRepository.findByInterestsContaining(interest)
        return ResponseEntity.ok(profiles)
    }
}