package com.first.projectswipe.data.models

import com.first.projectswipe.network.dto.UserDto

data class Message(
    val id: String,
    val sender: UserDto,
    val receiver: UserDto,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean,
    val messageType: String // "TEXT", "IMAGE", "FILE"
) {
    val isSentByCurrentUser: Boolean = false // Will be set based on current user ID in the view model
}