package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CardDto> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable).map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CardDto getCardById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        return convertToDto(card);
    }

    @Override
    @Transactional
    public CardDto createCard(CardCreateRequestDto requestDto) {
        cardRepository.findByCardNumber(requestDto.getCardNumber()).ifPresent(c -> {
            throw new IllegalArgumentException("Card with this number already exists.");
        });

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDto.getUserId()));

        Card card = new Card();
        card.setCardNumber(requestDto.getCardNumber());
        card.setOwner(user);
        card.setExpiryDate(requestDto.getExpiryDate());
        card.setBalance(requestDto.getBalance());
        card.setStatus(CardStatus.ACTIVE);

        Card savedCard = cardRepository.save(card);
        return convertToDto(savedCard);
    }

    @Override
    @Transactional
    public CardDto updateCardStatus(Long id, String status) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));

        try {
            CardStatus newStatus = CardStatus.valueOf(status.toUpperCase());
            card.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        Card updatedCard = cardRepository.save(card);
        return convertToDto(updatedCard);
    }


    @Override
    @Transactional
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Card not found with id: " + id);
        }
        cardRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardDto> getCardsByUserId(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return cardRepository.findByOwner(user, pageable).map(this::convertToDto);
    }

    @Override
    @Transactional
    public CardDto requestCardBlock(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));

        if (!card.getOwner().getId().equals(userId)) {
            throw new SecurityException("User " + userId + " does not have permission to block card " + cardId);
        }

        card.setStatus(CardStatus.BLOCKED);
        Card updatedCard = cardRepository.save(card);
        return convertToDto(updatedCard);
    }

    private CardDto convertToDto(Card card) {
        CardDto dto = new CardDto();
        dto.setId(card.getId());
        dto.setMaskedCardNumber(CardDto.maskCardNumber(card.getCardNumber()));
        dto.setOwnerUsername(card.getOwner().getUsername());
        dto.setExpiryDate(CardDto.formatExpiryDate(card.getExpiryDate()));
        dto.setStatus(card.getStatus());
        dto.setBalance(card.getBalance());
        return dto;
    }
} 