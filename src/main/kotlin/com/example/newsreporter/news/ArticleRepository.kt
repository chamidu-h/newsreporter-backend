package com.example.newsreporter.news

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {
    fun findByStatus(status: ArticleStatus): List<Article>
}
