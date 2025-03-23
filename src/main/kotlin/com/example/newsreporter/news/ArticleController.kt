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
    private val userRepository: UserRepository,  // Comma here
    private val reviewCommentRepository: ReviewCommentRepository
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

    @PutMapping("/{id}/reject")
fun rejectArticle(@PathVariable id: Long, @RequestBody payload: Map<String, String>): ResponseEntity<Article> {
    val article = articleRepository.findById(id)
        .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found") }
    
    // Update article status to REJECTED and update timestamp
    article.status = ArticleStatus.REJECTED
    article.updatedAt = LocalDateTime.now()
    articleRepository.save(article)

    // Save the review comment
    val commentText = payload["comment"] ?: ""
    val editorId = 4L  // Replace with actual logic to retrieve the editor's id
    val reviewComment = ReviewComment(
        articleId = article.id,
        editorId = editorId,
        comment = commentText,
        createdAt = LocalDateTime.now()
    )
    reviewCommentRepository.save(reviewComment)

    return ResponseEntity.ok(article)
}

    
    @GetMapping("/rejected")
    fun getRejectedArticles(): ResponseEntity<List<Article>> {
        val articles = articleRepository.findByStatus(ArticleStatus.REJECTED)
        // Optionally, you can join with review comments to include them in the response
        return ResponseEntity.ok(articles)
    }
}
