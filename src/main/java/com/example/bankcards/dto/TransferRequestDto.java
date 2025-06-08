package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {

    @NotNull(message = "Source card ID cannot be null")
    private Long fromCardId;

    @NotNull(message = "Destination card ID cannot be null")
    private Long toCardId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Transfer amount must be positive")
    private BigDecimal amount;
} 