package com.first.devswipe.controller

import com.first.devswipe.dto.*
import com.first.devswipe.entity.User
import com.first.devswipe.repository.UserRepository
import com.first.devswipe.security.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        // Check if user already exists
        if (userRepository.existsByEmail(request.email)) {
            return ResponseEntity.badRequest().build()
        }
        if (userRepository.existsByDisplayUsername(request.username)) {
            return ResponseEntity.badRequest().build()
        }

        // Create new user
        val user = User(
            displayUsername = request.username,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            firstName = request.firstName,
            lastName = request.lastName
        )

        val savedUser = userRepository.save(user)
        val token = jwtUtil.generateToken(savedUser)

        return ResponseEntity.ok(
            AuthResponse(
                token = token,
                user = UserDto(
                    id = savedUser.id!!,
                    username = savedUser.displayUsername,
                    email = savedUser.email,
                    firstName = savedUser.firstName,
                    lastName = savedUser.lastName
                )
            )
        )
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        // Authenticate user
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        val user = userRepository.findByEmail(request.email)
            ?: return ResponseEntity.badRequest().build()

        val token = jwtUtil.generateToken(user)

        return ResponseEntity.ok(
            AuthResponse(
                token = token,
                user = UserDto(
                    id = user.id!!,
                    username = user.displayUsername,
                    email = user.email,
                    firstName = user.firstName,
                    lastName = user.lastName
                )
            )
        )
    }

    @GetMapping("/me")
    fun getCurrentUser(@RequestHeader("Authorization") authorization: String): ResponseEntity<UserDto> {
        val token = authorization.substring(7) // Remove "Bearer "
        val email = jwtUtil.extractUsername(token)

        val user = userRepository.findByEmail(email)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(
            UserDto(
                id = user.id!!,
                username = user.displayUsername,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName
            )
        )
    }
}