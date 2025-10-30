package com.first.devswipe.dto

import com.first.devswipe.entity.MessageType
import java.util.*

data class MessageRequest(
    val receiverId: UUID,
    val content: String,
    val messageType: MessageType = MessageType.TEXT
)

data class MessageResponse(
    val id: UUID,
    val sender: UserDto,
    val receiver: UserDto,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean,
    val messageType: MessageType
)

data class ConversationResponse(
    val id: UUID,
    val otherUser: UserDto,
    val lastMessage: String?,
    val lastMessageTime: Long?,
    val unreadCount: Int
)

data class SendMessageResponse(
    val success: Boolean,
    val message: MessageResponse?,
    val error: String?
)