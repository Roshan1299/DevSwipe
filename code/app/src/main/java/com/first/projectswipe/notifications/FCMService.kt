package com.first.projectswipe.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.first.projectswipe.MainActivity
import com.first.projectswipe.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType

class FCMService : FirebaseMessagingService() {

    private val notificationTag = "devswipe_notification"
    private val notificationId = 1001

    override fun onNewToken(token: String) {
        Log.d("FCMService", "Refreshed token: $token")
        
        // Register the new token with our backend
        registerTokenWithBackend(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d("FCMService", "From: ${remoteMessage.from}")

        // Check if message contains a data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCMService", "Message data payload: ${remoteMessage.data}")
            
            val title = remoteMessage.data["title"] ?: "DevSwipe"
            val body = remoteMessage.data["body"] ?: "You have a new notification"
            val type = remoteMessage.data["type"] ?: "message"
            
            showNotification(title, body, type)
        }

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d("FCMService", "Message Notification Body: ${it.body}")
            showNotification(it.title ?: "DevSwipe", it.body ?: "You have a new notification", "message")
        }
    }

    private fun showNotification(title: String, body: String, type: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationTag,
                "DevSwipe Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for DevSwipe app"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create an intent to open the app when notification is tapped
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", type)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, notificationTag)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Using app icon
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun registerTokenWithBackend(token: String) {
        // Get JWT token from SharedPreferences directly
        val sharedPreferences = applicationContext.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("jwt_token", null)

        if (!jwtToken.isNullOrEmpty()) {
            // Make API call directly using a temporary HTTP client
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                try {
                    // Create a minimal HTTP client to make the API call
                    val client = okhttp3.OkHttpClient()
                    val json = org.json.JSONObject()
                    json.put("token", token)

                    val body = okhttp3.RequestBody.create(
                        "application/json; charset=utf-8".toMediaType(),
                        json.toString()
                    )

                    val request = okhttp3.Request.Builder()
                        .url("http://10.0.2.2:8080/api/notifications/register-token") // Update if needed
                        .post(body)
                        .addHeader("Authorization", "Bearer $jwtToken")
                        .addHeader("Content-Type", "application/json")
                        .build()

                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        Log.d("FCMService", "Token registered successfully with backend")
                    } else {
                        Log.e("FCMService", "Failed to register token: ${response.code}")
                    }
                    response.close()
                } catch (e: Exception) {
                    Log.e("FCMService", "Error registering token with backend: ${e.message}")
                }
            }
        } else {
            Log.d("FCMService", "User not logged in, deferring token registration until login")
        }
    }
}