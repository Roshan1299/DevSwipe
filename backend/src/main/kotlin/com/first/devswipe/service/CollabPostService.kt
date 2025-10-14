package com.first.devswipe.service

import com.first.devswipe.dto.CollaborationCreateRequest
import com.first.devswipe.dto.UpdateCollaborationRequest
import com.first.devswipe.entity.CollabPost
import com.first.devswipe.entity.User
import com.first.devswipe.repository.CollabPostRepository
import com.first.devswipe.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CollabPostService(
    private val collabPostRepository: CollabPostRepository,
    private val userRepository: UserRepository
) {
    fun createCollabPost(userId: UUID, request: CollaborationCreateRequest): CollabPost {
        val user = userRepository.findById(userId)
            .orElseThrow { throw RuntimeException("User not found with id: $userId") }

        val collabPost = CollabPost(
            projectTitle = request.projectTitle,
            description = request.description,
            skillsNeeded = request.skillsNeeded,
            timeCommitment = request.timeCommitment,
            teamSize = request.teamSize,
            user = user
        )

        return collabPostRepository.save(collabPost)
    }

    fun updateCollabPost(postId: UUID, request: UpdateCollaborationRequest): CollabPost {
        val collabPost = collabPostRepository.findById(postId)
            .orElseThrow { throw RuntimeException("Collab post not found with id: $postId") }

        request.projectTitle?.let { collabPost.projectTitle = it }
        request.description?.let { collabPost.description = it }
        request.skillsNeeded?.let { collabPost.skillsNeeded = it }
        request.timeCommitment?.let { collabPost.timeCommitment = it }
        request.teamSize?.let { collabPost.teamSize = it }

        return collabPostRepository.save(collabPost)
    }

    fun getCollabPost(postId: UUID): CollabPost {
        return collabPostRepository.findById(postId)
            .orElseThrow { throw RuntimeException("Collab post not found with id: $postId") }
    }

    fun getCollabPostsByUserId(userId: UUID): List<CollabPost> {
        return collabPostRepository.findByUserId(userId)
    }

    fun getAllCollabPosts(): List<CollabPost> {
        return collabPostRepository.findAll()
    }

    fun deleteCollabPost(postId: UUID) {
        val collabPost = collabPostRepository.findById(postId)
            .orElseThrow { throw RuntimeException("Collab post not found with id: $postId") }
        
        collabPostRepository.delete(collabPost)
    }
}