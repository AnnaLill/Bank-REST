package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.exception.TransferException;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final CardRepository cardRepository;

    @Override
    @Transactional
    public void transferFunds(Long userId, TransferRequestDto requestDto) {
        if (requestDto.getFromCardId().equals(requestDto.getToCardId())) {
            throw new TransferException("Source and destination cards cannot be the same.");
        }

        Card fromCard = cardRepository.findById(requestDto.getFromCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Source card not found with id: " + requestDto.getFromCardId()));

        Card toCard = cardRepository.findById(requestDto.getToCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination card not found with id: " + requestDto.getToCardId()));

        if (!fromCard.getOwner().getId().equals(userId) || !toCard.getOwner().getId().equals(userId)) {
            throw new SecurityException("User does not own one or both of the cards.");
        }

        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new TransferException("Both cards must be active for the transfer.");
        }

        if (fromCard.getBalance().compareTo(requestDto.getAmount()) < 0) {
            throw new TransferException("Insufficient funds on the source card.");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(requestDto.getAmount()));
        toCard.setBalance(toCard.getBalance().add(requestDto.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }
} 