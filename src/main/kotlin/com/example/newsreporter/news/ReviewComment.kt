package com.example.newsreporter.news

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "review_comments")
data class ReviewComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "article_id", nullable = false)
    val articleId: Long,

    @Column(name = "editor_id", nullable = false)
    val editorId: Long,

    @Column(columnDefinition = "TEXT", nullable = false)
    val comment: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
