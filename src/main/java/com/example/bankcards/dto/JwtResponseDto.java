package com.example.bankcards.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO для ответа с JWT токеном аутентификации.
 * Содержит токен доступа и тип токена для клиентского приложения.
 * 
 * @author Анна Тебенькова
 * @version 1.0
 */
@Getter
@Setter
public class JwtResponseDto {
    
    /**
     * JWT токен для доступа к защищенным ресурсам
     */
    private String token;
    
    /**
     * Тип токена (всегда "Bearer" для JWT)
     */
    private final String type = "Bearer";

    /**
     * Конструктор для создания ответа с токеном.
     * 
     * @param token JWT токен
     */
    public JwtResponseDto(String token) {
        this.token = token;
    }
} 