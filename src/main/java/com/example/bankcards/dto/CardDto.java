package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Data
public class CardDto {
    private Long id;
    private String maskedCardNumber;
    private String ownerUsername;
    private String expiryDate;
    private CardStatus status;
    private BigDecimal balance;

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