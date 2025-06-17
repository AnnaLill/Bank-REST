package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;

/**
 * Сервис для выполнения переводов средств между картами.
 * Предоставляет функциональность для внутренних переводов между картами одного пользователя.
 * 
 * @author Анна Тебенькова
 * @version 1.0
 */
public interface TransferService {
    
    /**
     * Выполняет перевод средств между картами пользователя.
     * Пользователь может переводить средства только между своими картами.
     * 
     * @param userId идентификатор пользователя, выполняющего перевод
     * @param requestDto данные перевода (исходная карта, целевая карта, сумма)
     * @throws com.example.bankcards.exception.InsufficientFundsException если недостаточно средств
     * @throws com.example.bankcards.exception.UnauthorizedException если карты не принадлежат пользователю
     * @throws com.example.bankcards.exception.CardNotFoundException если карта не найдена
     */
    void transferFunds(Long userId, TransferRequestDto requestDto);
} 