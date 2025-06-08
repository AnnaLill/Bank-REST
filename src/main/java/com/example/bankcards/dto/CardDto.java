package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class CardDto {
    private Long id;
    private String maskedCardNumber;
    private String ownerUsername;
    private String expiryDate;
    private CardStatus status;
    private BigDecimal balance;

    public CardDto(Long id, String maskedCardNumber, BigDecimal balance, CardStatus status) {
        this.id = id;
        this.maskedCardNumber = maskedCardNumber;
        this.balance = balance;
        this.status = status;
    }

    private static final DateTimeFormatter EXPIRY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/yy");

    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() <= 4) {
            return cardNumber;
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    public static String formatExpiryDate(YearMonth expiryDate) {
        if (expiryDate == null) {
            return null;
        }
        return expiryDate.format(EXPIRY_DATE_FORMATTER);
    }
} 