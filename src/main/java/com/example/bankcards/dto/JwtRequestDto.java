package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса аутентификации пользователя.
 * Содержит учетные данные (имя пользователя и пароль) для получения JWT токена.
 * 
 * @author Анна Тебенькова
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequestDto {
    
    /**
     * Имя пользователя для входа в систему
     */
    @NotBlank(message = "Username cannot be blank")
    private String username;

    /**
     * Пароль пользователя
     */
    @NotBlank(message = "Password cannot be blank")
    private String password;
} 