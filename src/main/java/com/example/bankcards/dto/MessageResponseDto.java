package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * DTO для простых текстовых сообщений от сервера.
 * Используется для возврата информационных сообщений, например, 
 * подтверждений операций или уведомлений.
 *
 * @author Анна Тебенькова
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDto {
    
    /**
     * Текстовое сообщение от сервера
     */
    private String message;
} 