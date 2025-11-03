package com.first.projectswipe.domain.repository

import com.first.projectswipe.data.models.Conversation
import com.first.projectswipe.data.models.Message
import com.first.projectswipe.network.dto.*

interface ChatRepository {
    suspend fun sendMessage(messageRequest: MessageRequest): Result<SendMessageResponse>
    suspend fun getConversations(): Result<List<ConversationResponse>>
    suspend fun getConversationMessages(otherUserId: String, page: Int, size: Int): Result<List<MessageResponse>>
    suspend fun markMessagesAsRead(otherUserId: String): Result<Unit>
    suspend fun getUnreadCount(): Result<Long>
}