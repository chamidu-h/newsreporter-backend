package com.example.newsreporter.news

data class ArticleSubmissionRequest(
    val title: String,
    val category: String,
    val content: String,
    val reporterId: Long // Simplified: reporter ID is passed directly from the mobile app
)
