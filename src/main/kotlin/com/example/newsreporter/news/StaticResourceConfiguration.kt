package com.example.newsreporter.news

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.io.File

@Configuration
class StaticResourceConfiguration : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Build absolute path to "uploads" folder.
        val uploadPath = "file:" + System.getProperty("user.dir") + File.separator + "uploads" + File.separator
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(uploadPath)
    }
}
