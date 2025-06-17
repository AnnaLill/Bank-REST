package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * DTO для регистрации нового пользователя в системе.
 * Содержит минимально необходимые данные для создания учетной записи.
 * 
 * @author Анна Тебенькова
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    
    /**
     * Уникальное имя пользователя для входа в систему
     */
    @NotBlank(message = "Username cannot be blank")
    private String username;

    /**
     * Пароль для учетной записи
     */
    @NotBlank(message = "Password cannot be blank")
    private String password;
} 
 