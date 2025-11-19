package com.first.projectswipe.data.repository

import com.first.projectswipe.data.models.Conversation
import com.first.projectswipe.data.models.Message
import com.first.projectswipe.domain.repository.ChatRepository
import com.first.projectswipe.network.ApiService
import com.first.projectswipe.network.dto.*
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ChatRepository {
    
    override suspend fun sendMessage(messageRequest: MessageRequest): Result<SendMessageResponse> {
        return try {
            val response = apiService.sendMessage(messageRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to send message: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getConversations(): Result<List<ConversationResponse>> {
        return try {
            val response = apiService.getConversations()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get conversations: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getConversationMessages(
        otherUserId: String,
        page: Int,
        size: Int
    ): Result<List<MessageResponse>> {
        return try {
            val response = apiService.getConversationMessages(otherUserId, page, size)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get messages: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markMessagesAsRead(otherUserId: String): Result<Unit> {
        return try {
            val response = apiService.markMessagesAsRead(otherUserId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to mark messages as read: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUnreadCount(): Result<Long> {
        return try {
            val response = apiService.getUnreadCount()
            if (response.isSuccessful && response.body() != null) {
                val count = response.body()!!["unreadCount"] ?: 0L
                Result.success(count)
            } else {
                Result.failure(Exception("Failed to get unread count: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}