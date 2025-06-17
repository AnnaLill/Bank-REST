package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Сервис для управления банковскими картами.
 * Предоставляет методы для создания, получения, обновления и удаления карт.
 * Поддерживает как административные операции (для всех карт), так и пользовательские (для своих карт).
 * 
 * @author Анна Тебенькова
 * @version 1.0
 */
public interface CardService {

    /**
     * Получает постраничный список всех карт в системе.
     * Доступно только администраторам.
     * 
     * @param pageable параметры пагинации
     * @return страница с картами
     */
    Page<CardDto> getAllCards(Pageable pageable);

    /**
     * Получает карту по её идентификатору.
     * 
     * @param id идентификатор карты
     * @return данные карты
     * @throws com.example.bankcards.exception.CardNotFoundException если карта не найдена
     */
    CardDto getCardById(Long id);

    /**
     * Создает новую карту для указанного пользователя.
     * Доступно только администраторам.
     * 
     * @param requestDto данные для создания карты
     * @return созданная карта
     */
    CardDto createCard(CardCreateRequestDto requestDto);

    /**
     * Обновляет статус карты.
     * Доступно только администраторам.
     * 
     * @param id идентификатор карты
     * @param status новый статус
     * @return обновленная карта
     */
    CardDto updateCardStatus(Long id, String status);

    /**
     * Удаляет карту из системы.
     * Доступно только администраторам.
     * 
     * @param id идентификатор карты
     */
    void deleteCard(Long id);

    /**
     * Получает постраничный список карт конкретного пользователя.
     * 
     * @param userId идентификатор пользователя
     * @param pageable параметры пагинации
     * @return страница с картами пользователя
     */
    Page<CardDto> getCardsByUserId(Long userId, Pageable pageable);

    /**
     * Запрашивает блокировку карты пользователем.
     * Пользователь может блокировать только свои карты.
     * 
     * @param cardId идентификатор карты
     * @param userId идентификатор пользователя
     * @return заблокированная карта
     * @throws com.example.bankcards.exception.UnauthorizedException если карта не принадлежит пользователю
     */
    CardDto requestCardBlock(Long cardId, Long userId);
} 