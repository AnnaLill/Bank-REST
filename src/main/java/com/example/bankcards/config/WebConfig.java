package com.example.bankcards.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация веб-слоя приложения.
 * Настраивает CORS (Cross-Origin Resource Sharing) для разрешения
 * запросов с других доменов к API приложения.
 * 
 * @author Анна Тебенькова
 * @version 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Настраивает CORS политику для всех эндпоинтов приложения.
     * Разрешает запросы с любого источника (*) для методов GET, POST, PUT, PATCH, DELETE, OPTIONS.
     * 
     * @param registry реестр CORS настроек
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
} 