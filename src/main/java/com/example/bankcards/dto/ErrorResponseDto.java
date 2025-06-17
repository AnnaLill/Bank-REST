package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для передачи информации об ошибках.
 * Используется для стандартизированного представления ошибок API,
 * включая временную метку, код статуса и детали ошибки.
 * 
 * @author Анна Тебенькова
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    
    /**
     * Временная метка возникновения ошибки
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * HTTP код статуса ошибки
     */
    private int status;
    
    /**
     * Тип ошибки (например, "Bad Request", "Not Found")
     */
    private String error;
    
    /**
     * Детальное сообщение об ошибке
     */
    private String message;
    
    /**
     * Путь запроса, на котором произошла ошибка
     */
    private String path;
} 