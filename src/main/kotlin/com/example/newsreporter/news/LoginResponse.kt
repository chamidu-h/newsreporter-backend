package com.example.newsreporter.news

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val userId: Long? = null,
    val role: String? = null
)
