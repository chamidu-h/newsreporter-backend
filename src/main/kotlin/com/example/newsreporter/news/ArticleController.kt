package com.example.newsreporter.news

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/articles")
@CrossOrigin
class ArticleController(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository
) {

    @PostMapping
    fun submitArticle(@RequestBody submission: ArticleSubmissionRequest): ResponseEntity<Article> {
        // Look up the reporter using the reporterId provided in the request
        val reporter = userRepository.findById(submission.reporterId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Reporter not found") }
        
        // Create the article with status SUBMITTED
        val article = Article(
            title = submission.title,
            category = submission.category,
            content = submission.content,
            author = reporter,
            status = ArticleStatus.SUBMITTED,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        articleRepository.save(article)
        return ResponseEntity.status(HttpStatus.CREATED).body(article)
    }

    @GetMapping("/submitted")
    fun getSubmittedArticles(): ResponseEntity<List<Article>> {
        val articles = articleRepository.findByStatus(ArticleStatus.SUBMITTED)
        return ResponseEntity.ok(articles)
    }

    @GetMapping("/{id}")
    fun getArticleById(@PathVariable id: Long): ResponseEntity<Article> {
        val article = articleRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found") }
        return ResponseEntity.ok(article)
    }
}

