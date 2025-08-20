package com.first.projectswipe.network

import com.first.projectswipe.network.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Authentication endpoints
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun getCurrentUser(@Header("Authorization") authorization: String): Response<UserDto>

    // User profile and onboarding endpoints
    @GET("api/users/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: String): Response<UserDto>

    @PUT("api/users/{userId}")
    suspend fun updateUser(@Path("userId") userId: String, @Body updateRequest: UpdateUserRequest): Response<UserDto>

    @PATCH("api/users/{userId}/skills")
    suspend fun updateUserSkills(@Path("userId") userId: String, @Body skillsRequest: UpdateSkillsRequest): Response<UserDto>

    @PATCH("api/users/{userId}/interests")
    suspend fun updateUserInterests(@Path("userId") userId: String, @Body interestsRequest: UpdateInterestsRequest): Response<UserDto>

    @PATCH("api/users/{userId}/onboarding")
    suspend fun completeOnboarding(@Path("userId") userId: String): Response<UserDto>
}