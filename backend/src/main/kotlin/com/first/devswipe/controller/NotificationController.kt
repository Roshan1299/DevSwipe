package com.first.devswipe.controller

import com.first.devswipe.dto.FcmTokenRequest
import com.first.devswipe.dto.NotificationResponse
import com.first.devswipe.entity.User
import com.first.devswipe.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val userRepository: UserRepository
) {

    @PostMapping("/register-token")
    fun registerToken(
        @AuthenticationPrincipal user: User,
        @RequestBody request: FcmTokenRequest
    ): ResponseEntity<NotificationResponse> {
        try {
            // Update the user's FCM token
            val updatedUser = user.copy(fcmToken = request.token)
            userRepository.save(updatedUser)
            
            return ResponseEntity.ok(
                NotificationResponse(
                    success = true,
                    message = "FCM token registered successfully"
                )
            )
        } catch (e: Exception) {
            val errorResponse = NotificationResponse(
                success = false,
                message = "Error registering FCM token: ${e.message ?: "Unknown error"}"
            )
            return ResponseEntity.status(500).body(errorResponse)
        }
    }

    @PostMapping("/unregister-token")
    fun unregisterToken(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<NotificationResponse> {
        try {
            // Remove the user's FCM token
            val updatedUser = user.copy(fcmToken = null)
            userRepository.save(updatedUser)
            
            return ResponseEntity.ok(
                NotificationResponse(
                    success = true,
                    message = "FCM token unregistered successfully"
                )
            )
        } catch (e: Exception) {
            val errorResponse = NotificationResponse(
                success = false,
                message = "Error unregistering FCM token: ${e.message ?: "Unknown error"}"
            )
            return ResponseEntity.status(500).body(errorResponse)
        }
    }
}