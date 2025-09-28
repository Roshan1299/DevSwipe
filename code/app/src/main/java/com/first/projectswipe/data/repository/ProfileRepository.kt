// data/repository/ProfileRepository.kt
package com.first.projectswipe.data.repository

import com.first.projectswipe.network.ApiService
import com.first.projectswipe.network.dto.*
import com.first.projectswipe.presentation.ui.auth.AuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager
) {

    /**
     * Get current user's profile from the /api/profile endpoint
     */
    suspend fun getCurrentProfile(): Result<UserProfileResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getCurrentProfile()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Profile not found. Please complete your profile setup."
                    401 -> "Unauthorized. Please login again."
                    403 -> "Access denied."
                    500 -> "Server error. Please try again later."
                    else -> "Failed to fetch profile: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Get another user's profile by ID from the /api/users/{userId} endpoint
     * This returns UserDto which we convert to UserProfileResponse
     */
    suspend fun getUserProfile(userId: String): Result<UserProfileResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getUserProfile(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "User profile not found."
                    401 -> "Unauthorized. Please login again."
                    403 -> "Access denied."
                    500 -> "Server error. Please try again later."
                    else -> "Failed to fetch user profile: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Update user profile using /api/profile endpoint
     */
    suspend fun updateProfile(request: UpdateUserRequest): Result<UserProfileResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.createOrUpdateProfile(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Invalid profile data. Please check your input."
                    401 -> "Unauthorized. Please login again."
                    422 -> "Validation error. Please check required fields."
                    else -> "Failed to update profile: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Update user using /api/users/{userId} endpoint
     */
    suspend fun updateUser(userId: String, request: UpdateUserRequest): Result<UserProfileResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.updateUser(userId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Invalid user data. Please check your input."
                    401 -> "Unauthorized. Please login again."
                    404 -> "User not found."
                    else -> "Failed to update user: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Update user skills using /api/users/{userId}/skills endpoint
     */
    suspend fun updateUserSkills(userId: String, skills: List<String>): Result<UserProfileResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = UpdateSkillsRequest(skills)
            val response = apiService.updateUserSkills(userId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update skills: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Update user interests using /api/users/{userId}/interests endpoint
     */
    suspend fun updateUserInterests(userId: String, interests: List<String>): Result<UserProfileResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = UpdateInterestsRequest(interests)
            val response = apiService.updateUserInterests(userId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update interests: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Complete onboarding using /api/users/{userId}/onboarding endpoint
     */
    suspend fun completeOnboarding(userId: String): Result<UserProfileResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.completeOnboarding(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Cannot complete onboarding. Profile may be incomplete."
                    401 -> "Unauthorized. Please login again."
                    404 -> "User not found."
                    else -> "Failed to complete onboarding: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Complete onboarding using /api/profile/complete-onboarding endpoint
     */
    suspend fun completeOnboardingProfile(): Result<UserProfileResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.completeOnboarding()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Cannot complete onboarding. Profile may be incomplete."
                    401 -> "Unauthorized. Please login again."
                    else -> "Failed to complete onboarding: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Search users by skill
     */
    suspend fun searchUsersBySkill(skill: String): Result<List<UserProfileResponse>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getUsersBySkill(skill)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to search users by skill: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Search users by interest
     */
    suspend fun searchUsersByInterest(interest: String): Result<List<UserProfileResponse>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getUsersByInterest(interest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to search users by interest: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    /**
     * Helper methods - these are not suspend functions since AuthManager methods are synchronous
     */
    fun isUserAuthenticated(): Boolean {
        return authManager.isUserLoggedIn()
    }

    fun getCurrentUserId(): String? {
        return authManager.getCurrentUserId()
    }

    /**
     * Convenience methods for current user updates
     */
    suspend fun updateCurrentUserSkills(skills: List<String>): Result<UserProfileResponse> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
        return updateUserSkills(userId, skills)
    }

    suspend fun updateCurrentUserInterests(interests: List<String>): Result<UserProfileResponse> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
        return updateUserInterests(userId, interests)
    }

    suspend fun completeCurrentUserOnboarding(): Result<UserProfileResponse> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
        return completeOnboarding(userId)
    }
}