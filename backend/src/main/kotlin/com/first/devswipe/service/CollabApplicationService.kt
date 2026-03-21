package com.first.devswipe.service

import com.first.devswipe.entity.CollabApplication
import com.first.devswipe.entity.Message
import com.first.devswipe.entity.MessageType
import com.first.devswipe.entity.Conversation
import com.first.devswipe.entity.User
import com.first.devswipe.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class CollabApplicationService(
    private val collabApplicationRepository: CollabApplicationRepository,
    private val collabPostRepository: CollabPostRepository,
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository
) {

    fun apply(applicantId: UUID, collabPostId: UUID): CollabApplication {
        val collabPost = collabPostRepository.findById(collabPostId)
            .orElseThrow { RuntimeException("Collaboration post not found") }

        val applicant = userRepository.findById(applicantId)
            .orElseThrow { RuntimeException("User not found") }

        // Can't apply to own post
        if (collabPost.user.id == applicantId) {
            throw IllegalArgumentException("Cannot apply to your own collaboration post")
        }

        // Can't apply if post is not active
        if (collabPost.status != "active") {
            throw IllegalArgumentException("This collaboration post is no longer accepting applications")
        }

        // Can't apply twice
        if (collabApplicationRepository.existsByCollabPostIdAndApplicantId(collabPostId, applicantId)) {
            throw IllegalArgumentException("You have already applied to this collaboration post")
        }

        val application = CollabApplication(
            collabPost = collabPost,
            applicant = applicant
        )

        return collabApplicationRepository.save(application)
    }

    fun getApplicantsForPost(postId: UUID, ownerId: UUID): List<CollabApplication> {
        val collabPost = collabPostRepository.findById(postId)
            .orElseThrow { RuntimeException("Collaboration post not found") }

        // Only owner can view applicants
        if (collabPost.user.id != ownerId) {
            throw IllegalArgumentException("Only the post owner can view applicants")
        }

        return collabApplicationRepository.findByCollabPostId(postId)
    }

    fun getMyApplications(applicantId: UUID): List<CollabApplication> {
        return collabApplicationRepository.findByApplicantId(applicantId)
    }

    fun acceptApplication(postId: UUID, applicantId: UUID, ownerId: UUID): CollabApplication {
        val collabPost = collabPostRepository.findById(postId)
            .orElseThrow { RuntimeException("Collaboration post not found") }

        if (collabPost.user.id != ownerId) {
            throw IllegalArgumentException("Only the post owner can accept applicants")
        }

        val application = collabApplicationRepository.findByCollabPostIdAndApplicantId(postId, applicantId)
            ?: throw RuntimeException("Application not found")

        if (application.status != "PENDING") {
            throw IllegalArgumentException("This application has already been ${application.status.lowercase()}")
        }

        application.status = "ACCEPTED"
        val savedApplication = collabApplicationRepository.save(application)

        // Increment currentTeamSize
        collabPost.currentTeamSize += 1

        // If team is full, set status to filled
        if (collabPost.currentTeamSize >= collabPost.teamSize) {
            collabPost.status = "filled"
        }
        collabPostRepository.save(collabPost)

        // Create a chat conversation with welcome message
        createAcceptanceChat(collabPost.user, application.applicant, collabPost.projectTitle)

        return savedApplication
    }

    fun declineApplication(postId: UUID, applicantId: UUID, ownerId: UUID): CollabApplication {
        val collabPost = collabPostRepository.findById(postId)
            .orElseThrow { RuntimeException("Collaboration post not found") }

        if (collabPost.user.id != ownerId) {
            throw IllegalArgumentException("Only the post owner can decline applicants")
        }

        val application = collabApplicationRepository.findByCollabPostIdAndApplicantId(postId, applicantId)
            ?: throw RuntimeException("Application not found")

        if (application.status != "PENDING") {
            throw IllegalArgumentException("This application has already been ${application.status.lowercase()}")
        }

        application.status = "DECLINED"
        return collabApplicationRepository.save(application)
    }

    private fun createAcceptanceChat(owner: User, applicant: User, projectTitle: String) {
        val welcomeMessage = "You've been accepted to \"$projectTitle\"! Excited to work with you!"

        val message = Message(
            sender = owner,
            receiver = applicant,
            content = welcomeMessage,
            messageType = MessageType.TEXT
        )
        val savedMessage = messageRepository.save(message)

        // Create or update conversation
        var conversation = conversationRepository.findByUserIds(owner.id!!, applicant.id!!)
        if (conversation == null) {
            conversation = Conversation(
                user1 = owner,
                user2 = applicant,
                lastMessageContent = welcomeMessage,
                lastMessageTime = savedMessage.timestamp
            )
            conversationRepository.save(conversation)
        } else {
            conversation.lastMessageContent = welcomeMessage
            conversation.lastMessageTime = savedMessage.timestamp
            conversation.updatedAt = LocalDateTime.now()
            conversationRepository.save(conversation)
        }
    }
}
