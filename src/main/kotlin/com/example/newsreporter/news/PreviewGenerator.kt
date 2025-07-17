package com.example.newsreporter.news

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class PreviewGenerator {
    
    private val logger = LoggerFactory.getLogger(PreviewGenerator::class.java)
    private val objectMapper = jacksonObjectMapper() // ✅ Use jacksonObjectMapper() instead
    
    data class ContentBlock(
        val content: String,
        val type: String
    )
    
    fun generatePreview(contentJson: String, maxLength: Int = 200): String {
        logger.info("Generating preview from JSON content: $contentJson")
        
        if (contentJson.isBlank()) return "No preview available"
        
        return try {
            // Parse JSON array to extract text content
            val contentBlocks: List<ContentBlock> = objectMapper.readValue(contentJson)
            
            // Extract text from content blocks
            val textContent = extractTextFromBlocks(contentBlocks)
            logger.info("Extracted text: $textContent")
            
            // Generate clean preview
            generateCleanPreview(textContent, maxLength)
            
        } catch (e: Exception) {
            logger.error("Error parsing JSON content: ${e.message}")
            // Fallback: treat as plain text
            generateCleanPreview(contentJson, maxLength)
        }
    }
    
    private fun extractTextFromBlocks(blocks: List<ContentBlock>): String {
        return blocks
            .filter { it.type in listOf("HEADING", "SUBHEADING", "PARAGRAPH") }
            .map { it.content }
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }
    
    private fun generateCleanPreview(text: String, maxLength: Int): String {
        // Remove extra whitespace and normalize
        val cleanText = text.replace("\\s+".toRegex(), " ").trim()
        
        return if (cleanText.length > maxLength) {
            val truncateIndex = findSafeWordBreak(cleanText, maxLength)
            cleanText.substring(0, truncateIndex).trim() + "..."
        } else {
            cleanText
        }
    }
    
    private fun findSafeWordBreak(content: String, maxLength: Int): Int {
        if (content.length <= maxLength) return content.length
        
        var breakPoint = maxLength
        while (breakPoint > 0 && content[breakPoint] != ' ') {
            breakPoint--
        }
        
        return if (breakPoint > 0) breakPoint else maxLength
    }
}
