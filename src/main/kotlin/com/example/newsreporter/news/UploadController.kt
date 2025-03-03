package com.example.newsreporter.news

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import java.io.File
import java.net.InetAddress
import java.net.UnknownHostException

// UploadController.kt
@RestController
@RequestMapping("/api/uploads")
class UploadController {

    @PostMapping
    fun uploadImage(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, String>> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Empty file"))
        }

        val originalName = file.originalFilename ?: "image"
        // Determine file extension.
        val extension = if (originalName.endsWith(".tmp", ignoreCase = true)) {
            when (file.contentType) {
                "image/jpeg" -> ".jpg"
                "image/png" -> ".png"
                "image/gif" -> ".gif"
                else -> ".png"
            }
        } else {
            if (originalName.contains(".")) {
                originalName.substring(originalName.lastIndexOf("."))
            } else {
                ".png"
            }
        }

        // Create a unique filename.
        val baseName = if (originalName.contains(".")) originalName.substringBeforeLast(".") else originalName
        val filename = "${System.currentTimeMillis()}_${baseName}$extension"

        // Ensure uploads directory exists.
        val uploadDir = File(System.getProperty("user.dir") + File.separator + "uploads")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }
        val filePath = File(uploadDir, filename)
        try {
            file.transferTo(filePath)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save file", e)
        }

        // Construct the public URL. Optionally set PUBLIC_URL in your environment.
        val publicUrlBase = System.getenv("PUBLIC_URL") ?: "http://192.168.8.199:8080"
        val publicUrl = "$publicUrlBase/uploads/$filename"

        return ResponseEntity.ok(mapOf("url" to publicUrl))
    }

    private fun getLocalHostUrl(): String {
        return try {
            "http://${InetAddress.getLocalHost().hostAddress}:8080"
        } catch (e: UnknownHostException) {
            "http://localhost:8080"
        }
    }
}
