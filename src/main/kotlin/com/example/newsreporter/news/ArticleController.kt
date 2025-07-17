package com.example.newsreporter.news

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class ArticleController(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val reviewCommentRepository: ReviewCommentRepository,
    private val previewGenerator: PreviewGenerator // NEW: Inject PreviewGenerator
) {

    @PostMapping
    fun submitArticle(@RequestBody submission: ArticleSubmissionRequest): ResponseEntity<Article> {
        println("Submit article endpoint called") // Debug log
        
        val reporter = userRepository.findById(submission.reporterId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Reporter not found") }
        
        val article = Article(
            title = submission.title,
            category = submission.category,
            content = submission.content,
            preview = previewGenerator.generatePreview(submission.content), // NEW: Generate preview
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
        println("Get submitted articles endpoint called") // Debug log
        val articles = articleRepository.findByStatus(ArticleStatus.SUBMITTED)
        return ResponseEntity.ok(articles)
    }

    @GetMapping("/{id}")
    fun getArticleById(@PathVariable id: Long): ResponseEntity<Article> {
        println("Get article by ID endpoint called: $id") // Debug log
        val article = articleRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found") }
        return ResponseEntity.ok(article)
    }

    @PutMapping("/{id}/approve")
    fun approveArticle(@PathVariable id: Long): ResponseEntity<Article> {
        println("✅ APPROVE ENDPOINT CALLED FOR ARTICLE ID: $id") // Debug log
        
        val article = articleRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found") }
        
        article.status = ArticleStatus.APPROVED
        article.updatedAt = LocalDateTime.now()
        articleRepository.save(article)

        println("✅ ARTICLE $id APPROVED SUCCESSFULLY") // Debug log
        return ResponseEntity.ok(article)
    }

    @PutMapping("/{id}/reject")
    fun rejectArticle(@PathVariable id: Long, @RequestBody payload: Map<String, String>): ResponseEntity<Article> {
        println("Reject article endpoint called: $id") // Debug log
        
        val article = articleRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found") }
        
        article.status = ArticleStatus.REJECTED
        article.updatedAt = LocalDateTime.now()
        articleRepository.save(article)

        val commentText = payload["comment"] ?: ""
        val editorId = 4L
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
        println("Get rejected articles endpoint called") // Debug log
        val articles = articleRepository.findByStatus(ArticleStatus.REJECTED)
        return ResponseEntity.ok(articles)
    }

    @GetMapping("/all")
    fun getAllArticles(): ResponseEntity<List<Article>> {
        println("Get all articles endpoint called") // Debug log
        val articles = articleRepository.findAllOrderByUpdatedAtDesc()
        return ResponseEntity.ok(articles)
    }

    @GetMapping("/approved")
    fun getApprovedArticles(): ResponseEntity<List<Article>> {
        println("Get approved articles endpoint called") // Debug log
        val articles = articleRepository.findByStatus(ArticleStatus.APPROVED)
        return ResponseEntity.ok(articles)
    }

    @GetMapping("/user/{userId}")
    fun getUserArticles(@PathVariable userId: Long): ResponseEntity<List<Article>> {
        println("Get user articles endpoint called: $userId") // Debug log
        val articles = articleRepository.findByAuthorId(userId)
        return ResponseEntity.ok(articles)
    }
    // NEW: Extension function to convert Article to ArticleSubmissionResponse
    private fun Article.toResponse(): ArticleSubmissionResponse {
        return ArticleSubmissionResponse(
            id = this.id,
            title = this.title,
            category = this.category,
            content = this.content,
            preview = this.preview, // Include preview field
            status = this.status.name,
            authorId = this.author.id,
            createdAt = this.createdAt.toString(),
            reviewComment = null // You can populate this from ReviewComment if needed
        )
    }

    @GetMapping("/user/{userId}/status/{status}")
    fun getUserArticlesByStatus(
        @PathVariable userId: Long, 
        @PathVariable status: String
    ): ResponseEntity<List<Article>> {
        println("Get user articles by status endpoint called: $userId, $status") // Debug log
        
        val articleStatus = try {
            ArticleStatus.valueOf(status.uppercase())
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: $status")
        }
        
        val articles = articleRepository.findByAuthorIdAndStatus(userId, articleStatus)
        return ResponseEntity.ok(articles)
    }
}
