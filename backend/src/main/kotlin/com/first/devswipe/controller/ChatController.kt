package com.first.devswipe.controller

import com.first.devswipe.dto.*
import com.first.devswipe.entity.MessageType
import com.first.devswipe.entity.User
import com.first.devswipe.service.ChatService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatService: ChatService
) {

    @PostMapping("/messages")
    fun sendMessage(
        @AuthenticationPrincipal user: User,
        @RequestBody request: MessageRequest
    ): ResponseEntity<SendMessageResponse> {
        val result = chatService.sendMessage(
            user,
            request.receiverId,
            request.content,
            request.messageType
        )
        
        return if (result.success) {
            ResponseEntity.ok(result)
        } else {
            ResponseEntity.badRequest().body(result)
        }
    }

    @GetMapping("/conversations")
    fun getConversations(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<List<ConversationResponse>> {
        val conversations = chatService.getConversations(user.id!!)
        return ResponseEntity.ok(conversations)
    }

    @GetMapping("/messages/{otherUserId}")
    fun getConversationMessages(
        @AuthenticationPrincipal user: User,
        @PathVariable otherUserId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<MessageResponse>> {
        val messages = chatService.getConversationMessages(user.id!!, otherUserId, page, size)
        return ResponseEntity.ok(messages)
    }

    @PostMapping("/messages/{otherUserId}/mark-as-read")
    fun markMessagesAsRead(
        @AuthenticationPrincipal user: User,
        @PathVariable otherUserId: UUID
    ): ResponseEntity<Map<String, String>> {
        chatService.markMessagesAsRead(user.id!!, otherUserId)
        return ResponseEntity.ok(mapOf("message" to "Messages marked as read"))
    }

    @GetMapping("/unread-count")
    fun getUnreadCount(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<Map<String, Long>> {
        val count = chatService.getUnreadCount(user.id!!)
        return ResponseEntity.ok(mapOf("unreadCount" to count))
    }
}