package com.example.bankcards.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * DTO для создания новой банковской карты.
 * Содержит все необходимые данные для создания карты пользователя.
 * 
 * @author Анна Тебенькова
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardCreateRequestDto {

    /**
     * Идентификатор пользователя, для которого создается карта
     */
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    /**
     * Номер карты (13-19 цифр)
     */
    @NotBlank(message = "Card number cannot be blank")
    @Pattern(regexp = "^[0-9]{13,19}$", message = "Card number must be between 13 and 19 digits")
    private String cardNumber;

    /**
     * Дата истечения срока действия карты
     */
    @NotNull(message = "Expiry date cannot be null")
    @Future(message = "Expiry date must be in the future")
    private YearMonth expiryDate;

    /**
     * Начальный баланс карты
     */
    @NotNull(message = "Balance cannot be null")
    @PositiveOrZero(message = "Balance must be zero or positive")
    private BigDecimal balance;
} 