package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * DTO для передачи данных о банковской карте.
 * Содержит маскированный номер карты и основную информацию о карте.
 * 
 * @author Анна Тебенькова
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    
    /**
     * Уникальный идентификатор карты
     */
    private Long id;
    
    /**
     * Маскированный номер карты (формат: **** **** **** 1234)
     */
    private String maskedCardNumber;
    
    /**
     * Имя пользователя владельца карты
     */
    private String ownerUsername;
    
    /**
     * Дата истечения срока действия в формате MM/yy
     */
    private String expiryDate;
    
    /**
     * Статус карты
     */
    private CardStatus status;
    
    /**
     * Баланс карты
     */
    private BigDecimal balance;

    /**
     * Конструктор для создания DTO с основными полями.
     * 
     * @param id идентификатор карты
     * @param maskedCardNumber маскированный номер карты
     * @param balance баланс карты
     * @param status статус карты
     */
    public CardDto(Long id, String maskedCardNumber, BigDecimal balance, CardStatus status) {
        this.id = id;
        this.maskedCardNumber = maskedCardNumber;
        this.balance = balance;
        this.status = status;
    }

    /**
     * Форматтер для даты истечения срока действия
     */
    private static final DateTimeFormatter EXPIRY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/yy");

    /**
     * Маскирует номер карты, оставляя видимыми только последние 4 цифры.
     * 
     * @param cardNumber исходный номер карты
     * @return маскированный номер карты в формате "**** **** **** 1234"
     */
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() <= 4) {
            return cardNumber;
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    /**
     * Форматирует дату истечения срока действия карты.
     * 
     * @param expiryDate дата истечения срока действия
     * @return отформатированная дата в формате "MM/yy" или null если дата не указана
     */
    public static String formatExpiryDate(YearMonth expiryDate) {
        if (expiryDate == null) {
            return null;
        }
        return expiryDate.format(EXPIRY_DATE_FORMATTER);
    }
} 