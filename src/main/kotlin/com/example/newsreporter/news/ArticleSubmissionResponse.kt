package com.example.newsreporter.news

data class ArticleSubmissionResponse(
    val id: Long,
    val title: String,
    val category: String,
    val content: String,
    val preview: String? = null, // NEW: Add preview field
    val status: String,
    val authorId: Long,
    val createdAt: String,
    var reviewComment: String? = null
)
