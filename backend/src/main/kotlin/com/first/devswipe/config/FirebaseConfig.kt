package com.first.devswipe.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@Configuration
class FirebaseConfig {

    @Bean
    @Profile("!test")  // Only create this bean when NOT in test profile
    fun firebaseMessaging(): FirebaseMessaging {
        // Load credentials from environment variable or classpath file
        val envJson = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON")
        val resource = ClassPathResource("firebase-service-account.json")
        val inputStream = when {
            !envJson.isNullOrBlank() -> ByteArrayInputStream(envJson.toByteArray(StandardCharsets.UTF_8))
            resource.exists() -> resource.inputStream
            else -> throw IllegalStateException("Firebase service account not found. Provide credentials or use different profile.")
        }

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(inputStream))
            .build()

        val app = if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        } else {
            FirebaseApp.getInstance()
        }

        return FirebaseMessaging.getInstance(app)
    }
}