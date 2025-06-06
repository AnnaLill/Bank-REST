package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {

    Page<CardDto> getAllCards(Pageable pageable);

    CardDto getCardById(Long id);

    CardDto createCard(CardCreateRequestDto requestDto);

    CardDto updateCardStatus(Long id, String status);

    void deleteCard(Long id);
} 