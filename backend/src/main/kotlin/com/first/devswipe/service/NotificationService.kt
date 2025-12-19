package com.first.devswipe.service

import com.first.devswipe.dto.NotificationRequest
import com.first.devswipe.dto.NotificationResponse
import com.first.devswipe.repository.UserRepository
import com.google.firebase.messaging.*
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class NotificationService(
    private val firebaseMessaging: FirebaseMessaging,
    private val userRepository: UserRepository
) {
    private val logger = Logger.getLogger(NotificationService::class.java.name)

    suspend fun sendNotification(notificationRequest: NotificationRequest): NotificationResponse {
        try {
            val user = userRepository.findById(notificationRequest.userId)
                .orElseThrow { Exception("User not found") }
            
            val fcmToken = user.fcmToken
            if (fcmToken.isNullOrEmpty()) {
                return NotificationResponse(
                    success = false,
                    message = "User does not have an FCM token registered"
                )
            }

            val message = Message.builder()
                .setToken(fcmToken)
                .setNotification(
                    Notification.builder()
                        .setTitle(notificationRequest.title)
                        .setBody(notificationRequest.body)
                        .build()
                )
                .putData("type", notificationRequest.type)
                .putData("userId", notificationRequest.userId.toString())
                .apply {
                    notificationRequest.data?.forEach { (key, value) ->
                        putData(key, value)
                    }
                }
                .build()

            val response = firebaseMessaging.send(message)
            logger.info("Successfully sent message: $response")
            
            return NotificationResponse(
                success = true,
                message = "Notification sent successfully"
            )
        } catch (e: Exception) {
            logger.severe("Error sending notification: ${e.message}")
            return NotificationResponse(
                success = false,
                message = "Error sending notification: ${e.message}"
            )
        }
    }

    suspend fun sendNotificationToToken(fcmToken: String, title: String, body: String, data: Map<String, String>? = null): NotificationResponse {
        try {
            val message = Message.builder()
                .setToken(fcmToken)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )
                .apply {
                    data?.forEach { (key, value) ->
                        putData(key, value)
                    }
                }
                .build()

            val response = firebaseMessaging.send(message)
            logger.info("Successfully sent message: $response")
            
            return NotificationResponse(
                success = true,
                message = "Notification sent successfully"
            )
        } catch (e: Exception) {
            logger.severe("Error sending notification: ${e.message}")
            return NotificationResponse(
                success = false,
                message = "Error sending notification: ${e.message}"
            )
        }
    }

    // Specialized method for sending message notifications
    suspend fun sendNewMessageNotification(senderName: String, recipientToken: String, messageContent: String): NotificationResponse {
        return sendNotificationToToken(
            fcmToken = recipientToken,
            title = "New Message from $senderName",
            body = messageContent,
            data = mapOf(
                "type" to "message",
                "senderName" to senderName,
                "messageContent" to messageContent
            )
        )
    }
}