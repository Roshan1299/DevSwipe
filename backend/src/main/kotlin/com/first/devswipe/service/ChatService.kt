package com.first.devswipe.service

import com.first.devswipe.dto.*
import com.first.devswipe.entity.Conversation
import com.first.devswipe.entity.Message
import com.first.devswipe.entity.MessageType
import com.first.devswipe.entity.User
import com.first.devswipe.repository.ConversationRepository
import com.first.devswipe.repository.MessageRepository
import com.first.devswipe.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class ChatService(
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository,
    private val notificationService: NotificationService
) {

    fun sendMessage(sender: User, receiverId: UUID, content: String, messageType: MessageType): SendMessageResponse {
        try {
            // Check if receiver exists
            val receiverOpt = userRepository.findById(receiverId)
            if (receiverOpt.isEmpty) {
                return SendMessageResponse(
                    success = false,
                    message = null,
                    error = "Receiver not found"
                )
            }
            val receiver = receiverOpt.get()

            // Ensure sender and receiver are different users
            if (sender.id == receiverId) {
                return SendMessageResponse(
                    success = false,
                    message = null,
                    error = "Cannot send message to yourself"
                )
            }

            // Create the message
            val message = Message(
                sender = sender,
                receiver = receiver,
                content = content,
                messageType = messageType
            )

            val savedMessage = messageRepository.save(message)

            // Update or create conversation
            updateOrCreateConversation(sender, receiver, savedMessage)

            // Send notification to receiver if they have an FCM token
            if (receiver.fcmToken != null) {
                notificationService.sendNewMessageNotification(
                    senderName = sender.displayName,
                    recipientToken = receiver.fcmToken!!,
                    messageContent = content
                )
            }

            // Create response DTO
            val messageResponse = MessageResponse(
                id = savedMessage.id!!,
                sender = UserDto(
                    id = sender.id!!,
                    username = sender.displayName,
                    email = sender.email,
                    firstName = sender.firstName,
                    lastName = sender.lastName,
                    university = sender.university
                ),
                receiver = UserDto(
                    id = receiver.id!!,
                    username = receiver.displayName,
                    email = receiver.email,
                    firstName = receiver.firstName,
                    lastName = receiver.lastName,
                    university = receiver.university
                ),
                content = savedMessage.content,
                timestamp = savedMessage.timestamp.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
                isRead = savedMessage.isRead,
                messageType = savedMessage.messageType
            )

            return SendMessageResponse(
                success = true,
                message = messageResponse,
                error = null
            )
        } catch (e: Exception) {
            return SendMessageResponse(
                success = false,
                message = null,
                error = "Error sending message: ${e.message}"
            )
        }
    }

    fun getConversationMessages(
        currentUserId: UUID,
        otherUserId: UUID,
        page: Int,
        size: Int
    ): List<MessageResponse> {
        val pageable = PageRequest.of(page, size)
        val messagesPage = messageRepository.findMessagesBetweenUsers(currentUserId, otherUserId, pageable)

        return messagesPage.content.map { message ->
            val sender = if (message.sender.id == currentUserId) message.sender else message.receiver
            val receiver = if (message.sender.id == currentUserId) message.receiver else message.sender

            MessageResponse(
                id = message.id!!,
                sender = UserDto(
                    id = message.sender.id!!,
                    username = message.sender.displayName,
                    email = message.sender.email,
                    firstName = message.sender.firstName,
                    lastName = message.sender.lastName,
                    university = message.sender.university
                ),
                receiver = UserDto(
                    id = message.receiver.id!!,
                    username = message.receiver.displayName,
                    email = message.receiver.email,
                    firstName = message.receiver.firstName,
                    lastName = message.receiver.lastName,
                    university = message.receiver.university
                ),
                content = message.content,
                timestamp = message.timestamp.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
                isRead = message.isRead,
                messageType = message.messageType
            )
        }
    }

    fun getConversations(userId: UUID): List<ConversationResponse> {
        val conversations = conversationRepository.findConversationsByUserId(userId)
        
        return conversations.map { conversation ->
            val otherUser = if (conversation.user1.id == userId) conversation.user2 else conversation.user1
            
            // Count unread messages from this specific conversation
            val unreadCount = messageRepository.countUnreadMessagesBetweenUsers(userId, otherUser.id!!)
            
            ConversationResponse(
                id = conversation.id!!,
                otherUser = UserDto(
                    id = otherUser.id!!,
                    username = otherUser.displayName,
                    email = otherUser.email,
                    firstName = otherUser.firstName,
                    lastName = otherUser.lastName,
                    university = otherUser.university
                ),
                lastMessage = conversation.lastMessageContent,
                lastMessageTime = conversation.lastMessageTime?.atZone(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
                unreadCount = unreadCount.toInt()
            )
        }
    }

    fun markMessagesAsRead(currentUserId: UUID, otherUserId: UUID) {
        val allUnreadMessages = messageRepository.findUnreadMessagesByUserId(currentUserId)
        
        // Filter messages that are from the other user
        val messagesToMarkAsRead = allUnreadMessages.filter { it.sender.id == otherUserId }
        
        messagesToMarkAsRead.forEach { message ->
            message.isRead = true
        }
        
        // Only save the messages that were actually updated
        if (messagesToMarkAsRead.isNotEmpty()) {
            messageRepository.saveAll(messagesToMarkAsRead)
        }
    }

    fun getUnreadCount(userId: UUID): Long {
        return messageRepository.countByReceiverIdAndIsReadFalse(userId)
    }

    private fun updateOrCreateConversation(sender: User, receiver: User, message: Message) {
        var conversation = conversationRepository.findByUserIds(sender.id!!, receiver.id!!)
        
        if (conversation == null) {
            conversation = Conversation(
                user1 = sender,
                user2 = receiver,
                lastMessageContent = message.content,
                lastMessageTime = message.timestamp
            )
            conversationRepository.save(conversation)
        } else {
            conversation.lastMessageContent = message.content
            conversation.lastMessageTime = message.timestamp
            conversation.updatedAt = LocalDateTime.now()
            conversationRepository.save(conversation)
        }
    }
}