package com.example.newsreporter.news

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime
import jakarta.persistence.*

@JsonIgnoreProperties(value = ["hibernateLazyInitializer", "handler"])
@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    @Column(unique = true)
    val email: String,
    val passwordHash: String,
    @Enumerated(EnumType.STRING)
    val role: Role,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class Role {
    REPORTER, EDITOR
}