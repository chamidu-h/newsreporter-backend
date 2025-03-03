package com.example.newsreporter.news

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepository: UserRepository
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val user = userRepository.findByEmail(loginRequest.email)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")
        
        // For a simple approach, perform a plain text check.
        // (In production, you should hash and salt passwords.)
        if (user.passwordHash != loginRequest.password) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
        }
        
        // Return the user details without a token.
        return ResponseEntity.ok(LoginResponse(true, "Login successful", user.id, user.role.name))
    }
}
