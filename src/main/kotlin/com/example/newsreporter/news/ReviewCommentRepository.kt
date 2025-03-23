package com.example.newsreporter.news

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import com.example.newsreporter.news.ReviewComment
import com.example.newsreporter.news.Article
import com.example.newsreporter.news.ArticleStatus


interface ReviewCommentRepository : JpaRepository<ReviewComment, Long> {
    fun findByArticleId(articleId: Long): ReviewComment?
}
