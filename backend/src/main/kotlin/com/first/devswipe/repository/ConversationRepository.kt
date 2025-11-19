package com.first.devswipe.repository

import com.first.devswipe.entity.Conversation
import com.first.devswipe.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ConversationRepository : JpaRepository<Conversation, UUID> {
    
    @Query("SELECT c FROM Conversation c WHERE c.user1.id = :userId OR c.user2.id = :userId ORDER BY c.updatedAt DESC")
    fun findConversationsByUserId(@Param("userId") userId: UUID): List<Conversation>
    
    @Query("SELECT c FROM Conversation c WHERE " +
           "(c.user1.id = :user1Id AND c.user2.id = :user2Id) OR " +
           "(c.user1.id = :user2Id AND c.user2.id = :user1Id)")
    fun findByUserIds(@Param("user1Id") user1Id: UUID, @Param("user2Id") user2Id: UUID): Conversation?
}