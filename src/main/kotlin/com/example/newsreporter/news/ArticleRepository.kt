package com.example.newsreporter.news

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {
    fun findByStatus(status: ArticleStatus): List<Article>
    
    // ✅ ADD MISSING METHODS REFERENCED IN CONTROLLER
    fun findByAuthor(author: User): List<Article>
    
    @Query("SELECT a FROM Article a WHERE a.author.id = :authorId")
    fun findByAuthorId(@Param("authorId") authorId: Long): List<Article>
    
    @Query("SELECT a FROM Article a WHERE a.author.id = :authorId AND a.status = :status")
    fun findByAuthorIdAndStatus(
        @Param("authorId") authorId: Long, 
        @Param("status") status: ArticleStatus
    ): List<Article>
    
    @Query("SELECT a FROM Article a ORDER BY a.updatedAt DESC")
    fun findAllOrderByUpdatedAtDesc(): List<Article>
    
    fun findByStatusIn(statuses: List<ArticleStatus>): List<Article>
}
