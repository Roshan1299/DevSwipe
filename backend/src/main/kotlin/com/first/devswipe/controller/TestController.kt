package com.first.devswipe.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/test")
class TestController {

    @GetMapping("/public")
    fun publicEndpoint(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("message" to "This is a public endpoint - no auth needed"))
    }

    @GetMapping("/protected")
    fun protectedEndpoint(authentication: Authentication): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(mapOf(
            "message" to "🎉 JWT is working! This is a protected endpoint",
            "user" to authentication.name,
            "authorities" to authentication.authorities.map { it.authority },
            "authenticated" to authentication.isAuthenticated
        ))
    }
}