package com.first.projectswipe.data.models

import com.first.projectswipe.network.dto.UserDto

data class Conversation(
    val id: String,
    val otherUser: UserDto,
    val lastMessage: String?,
    val lastMessageTime: Long?,
    val unreadCount: Int
)