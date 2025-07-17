package com.example.newsreporter.news

import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "articles")
data class Article(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val title: String,
    
    val category: String,  // New field for article category
    
    @Column(columnDefinition = "TEXT")
    val content: String,

    @Column(length = 300) // NEW: Add preview field
    val preview: String? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    val author: User,
    
    @Enumerated(EnumType.STRING)
    var status: ArticleStatus = ArticleStatus.SUBMITTED, // Articles submitted from the app are marked as SUBMITTED
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class ArticleStatus {
    SUBMITTED,
    APPROVED,
    REJECTED
}
