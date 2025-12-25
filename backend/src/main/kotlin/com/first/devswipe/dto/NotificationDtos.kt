package com.first.devswipe.dto

data class FcmTokenRequest(
    val token: String
)

data class NotificationRequest(
    val userId: java.util.UUID,
    val title: String,
    val body: String,
    val type: String = "message", // message, like, system, etc.
    val data: Map<String, String>? = null
)

data class NotificationResponse(
    val success: Boolean,
    val message: String
)