package com.first.projectswipe.network

import com.first.projectswipe.network.dto.*
import okhttp3.MultipartBody
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
    suspend fun getUserProfile(@Path("userId") userId: String): Response<UserProfileResponse>

    @PUT("api/users/{userId}")
    suspend fun updateUser(@Path("userId") userId: String, @Body updateRequest: UpdateUserRequest): Response<UserProfileResponse>

    @PATCH("api/users/{userId}/skills")
    suspend fun updateUserSkills(@Path("userId") userId: String, @Body skillsRequest: UpdateSkillsRequest): Response<UserProfileResponse>

    @PATCH("api/users/{userId}/interests")
    suspend fun updateUserInterests(@Path("userId") userId: String, @Body interestsRequest: UpdateInterestsRequest): Response<UserProfileResponse>

    @PATCH("api/users/{userId}/onboarding")
    suspend fun completeOnboarding(@Path("userId") userId: String): Response<UserProfileResponse>

    // Profile endpoints - add these to your existing ApiService
    @GET("api/profile")
    suspend fun getCurrentProfile(): Response<UserProfileResponse>

    @POST("api/profile")
    suspend fun createOrUpdateProfile(@Body request: UpdateUserRequest): Response<UserProfileResponse>

    @PUT("api/profile/complete-onboarding")
    suspend fun completeOnboarding(): Response<UserProfileResponse>

    @GET("api/profile/search/skills/{skill}")
    suspend fun getUsersBySkill(@Path("skill") skill: String): Response<List<UserProfileResponse>>

    @GET("api/profile/search/interests/{interest}")
    suspend fun getUsersByInterest(@Path("interest") interest: String): Response<List<UserProfileResponse>>

    @Multipart
    @POST("api/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Response<Map<String, String>>

    @POST("api/projects")
    suspend fun createProject(@Body request: ProjectCreateRequest): Response<ProjectResponse>

    @PUT("api/projects/{projectId}")
    suspend fun updateProject(@Path("projectId") projectId: String, @Body request: UpdateProjectRequest): Response<ProjectResponse>

    @GET("api/projects/{projectId}")
    suspend fun getProject(@Path("projectId") projectId: String): Response<ProjectResponse>

    @GET("api/projects/my-projects")
    suspend fun getCurrentUserProjects(): Response<List<ProjectResponse>>

    @DELETE("api/projects/{projectId}")
    suspend fun deleteProject(@Path("projectId") projectId: String): Response<Map<String, String>>

    @GET("api/projects")
    suspend fun getProjects(): Response<List<ProjectResponse>>

    @GET("api/projects/filter")
    suspend fun filterProjects(
        @Query("difficulty") difficulty: String?,
        @Query("tags") tags: List<String>?
    ): Response<List<ProjectResponse>>

    // Collaboration Post endpoints
    @POST("api/collaborations")
    suspend fun createCollaboration(@Body request: CollaborationCreateRequest): Response<CollaborationResponse>

    @PUT("api/collaborations/{postId}")
    suspend fun updateCollaboration(@Path("postId") postId: String, @Body request: UpdateCollaborationRequest): Response<CollaborationResponse>

    @GET("api/collaborations/{postId}")
    suspend fun getCollaboration(@Path("postId") postId: String): Response<CollaborationResponse>

    @GET("api/collaborations/my-collaborations")
    suspend fun getCurrentUserCollaborations(): Response<List<CollaborationResponse>>

    @DELETE("api/collaborations/{postId}")
    suspend fun deleteCollaboration(@Path("postId") postId: String): Response<Map<String, String>>

    @GET("api/collaborations")
    suspend fun getCollaborations(): Response<List<CollaborationResponse>>

    // Chat endpoints
    @POST("api/chat/messages")
    suspend fun sendMessage(@Body request: MessageRequest): Response<SendMessageResponse>

    @GET("api/chat/conversations")
    suspend fun getConversations(): Response<List<ConversationResponse>>

    @GET("api/chat/messages/{otherUserId}")
    suspend fun getConversationMessages(
        @Path("otherUserId") otherUserId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<List<MessageResponse>>

    @POST("api/chat/messages/{otherUserId}/mark-as-read")
    suspend fun markMessagesAsRead(@Path("otherUserId") otherUserId: String): Response<Map<String, String>>

    @GET("api/chat/unread-count")
    suspend fun getUnreadCount(): Response<Map<String, Long>>

    // Notification endpoints
    @POST("api/notifications/register-token")
    suspend fun registerFcmToken(@Body request: Map<String, String>): Response<Map<String, String>>

    @POST("api/notifications/unregister-token")
    suspend fun unregisterFcmToken(): Response<Map<String, String>>
}