package com.first.devswipe.repository

import com.first.devswipe.entity.Message
import com.first.devswipe.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MessageRepository : JpaRepository<Message, UUID> {
    
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR " +
           "(m.sender.id = :user2Id AND m.receiver.id = :user1Id) " +
           "ORDER BY m.timestamp ASC")
    fun findMessagesBetweenUsers(
        @Param("user1Id") user1Id: UUID,
        @Param("user2Id") user2Id: UUID,
        pageable: Pageable
    ): Page<Message>
    
    @Query("SELECT m FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false")
    fun findUnreadMessagesByUserId(@Param("userId") userId: UUID): List<Message>
    
    fun countByReceiverIdAndIsReadFalse(receiverId: UUID): Long
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false AND m.sender.id = :otherUserId")
    fun countUnreadMessagesBetweenUsers(
        @Param("userId") userId: UUID,
        @Param("otherUserId") otherUserId: UUID
    ): Long
    
    @Query("SELECT m FROM Message m WHERE m.sender.id = :senderId AND m.receiver.id = :receiverId ORDER BY m.timestamp DESC LIMIT 1")
    fun findLastMessageBetweenUsers(senderId: UUID, receiverId: UUID): Message?
}