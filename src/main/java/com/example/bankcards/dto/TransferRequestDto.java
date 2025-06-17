package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO для запроса перевода средств между картами.
 * Содержит информацию об исходной карте, целевой карте и сумме перевода.
 * 
 * @author Анна Тебенькова
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {

    /**
     * Идентификатор исходной карты (с которой списываются средства)
     */
    @NotNull(message = "Source card ID cannot be null")
    private Long fromCardId;

    /**
     * Идентификатор целевой карты (на которую зачисляются средства)
     */
    @NotNull(message = "Destination card ID cannot be null")
    private Long toCardId;

    /**
     * Сумма перевода
     */
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Transfer amount must be positive")
    private BigDecimal amount;
} 