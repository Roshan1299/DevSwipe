package com.first.projectswipe.data.repository

import com.first.projectswipe.data.models.ProjectIdea
import com.first.projectswipe.network.ApiService
import com.first.projectswipe.network.NetworkModule
import com.first.projectswipe.network.dto.ProjectResponse
import com.first.projectswipe.network.TokenProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenProvider: TokenProvider
) {

    suspend fun getCurrentUserProjects(): Result<List<ProjectIdea>> {
        return try {
            val token = tokenProvider.getToken()
            if (token == null) {
                return Result.failure(Exception("User not authenticated"))
            }

            val response = apiService.getCurrentUserProjects()
            if (response.isSuccessful) {
                val projectResponses = response.body() ?: emptyList()
                val projectIdeas = projectResponses.map { projectResponse ->
                    ProjectIdea(
                        id = projectResponse.id,
                        title = projectResponse.title,
                        previewDescription = projectResponse.previewDescription,
                        fullDescription = projectResponse.fullDescription,
                        createdBy = projectResponse.createdBy,
                        tags = projectResponse.tags,
                        difficulty = projectResponse.difficulty,
                        githubLink = projectResponse.githubLink
                    )
                }
                Result.success(projectIdeas)
            } else {
                Result.failure(Exception("Failed to fetch projects: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProject(projectId: String): Result<Boolean> {
        return try {
            val token = tokenProvider.getToken()
            if (token == null) {
                return Result.failure(Exception("User not authenticated"))
            }

            val response = apiService.deleteProject(projectId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to delete project: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}