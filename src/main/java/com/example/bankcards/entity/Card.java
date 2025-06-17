package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.example.bankcards.util.CardNumberConverter;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Сущность банковской карты.
 * Представляет банковскую карту пользователя с зашифрованным номером,
 * балансом и статусом.
 * 
 * @author Анна Тебенькова
 * @version 1.0
 */
@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"owner"})
public class Card {

    /**
     * Уникальный идентификатор карты
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Номер карты (зашифрованный в базе данных)
     */
    @Column(name = "card_number", nullable = false, unique = true)
    @Convert(converter = CardNumberConverter.class)
    private String cardNumber;

    /**
     * Владелец карты
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    /**
     * Дата истечения срока действия карты
     */
    @Column(nullable = false)
    private YearMonth expiryDate;

    /**
     * Статус карты (активна, заблокирована и т.д.)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;

    /**
     * Баланс карты
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
} 