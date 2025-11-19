package com.first.projectswipe.network.dto

import java.util.*

data class MessageRequest(
    val receiverId: String,
    val content: String,
    val messageType: String = "TEXT"
)

data class MessageResponse(
    val id: String,
    val sender: UserDto,
    val receiver: UserDto,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean,
    val messageType: String
)

data class ConversationResponse(
    val id: String,
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