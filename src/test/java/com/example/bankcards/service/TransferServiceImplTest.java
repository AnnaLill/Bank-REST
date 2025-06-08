package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.exception.TransferException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

    private User user;
    private Card fromCard;
    private Card toCard;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        fromCard = new Card();
        fromCard.setId(10L);
        fromCard.setOwner(user);
        fromCard.setBalance(new BigDecimal("1000.00"));
        fromCard.setStatus(CardStatus.ACTIVE);

        toCard = new Card();
        toCard.setId(20L);
        toCard.setOwner(user);
        toCard.setBalance(new BigDecimal("500.00"));
        toCard.setStatus(CardStatus.ACTIVE);
    }

    @Test
    void transferFunds_Success() {

        TransferRequestDto requestDto = new TransferRequestDto();
        requestDto.setFromCardId(fromCard.getId());
        requestDto.setToCardId(toCard.getId());
        requestDto.setAmount(new BigDecimal("100.00"));

        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));


        transferService.transferFunds(user.getId(), requestDto);


        assertEquals(new BigDecimal("900.00"), fromCard.getBalance());
        assertEquals(new BigDecimal("600.00"), toCard.getBalance());
        verify(cardRepository, times(1)).save(fromCard);
        verify(cardRepository, times(1)).save(toCard);
    }

    @Test
    void transferFunds_InsufficientFunds_ThrowsException() {

        TransferRequestDto requestDto = new TransferRequestDto();
        requestDto.setFromCardId(fromCard.getId());
        requestDto.setToCardId(toCard.getId());
        requestDto.setAmount(new BigDecimal("2000.00"));

        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));


        TransferException exception = assertThrows(TransferException.class, () -> {
            transferService.transferFunds(user.getId(), requestDto);
        });

        assertEquals("Insufficient funds on the source card.", exception.getMessage());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void transferFunds_FromCardNotFound_ThrowsException() {

        TransferRequestDto requestDto = new TransferRequestDto();
        requestDto.setFromCardId(99L);
        requestDto.setToCardId(toCard.getId());
        requestDto.setAmount(new BigDecimal("100.00"));

        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            transferService.transferFunds(user.getId(), requestDto);
        });

        assertEquals("Source card not found with id: 99", exception.getMessage());
    }

    @Test
    void transferFunds_ToCardNotFound_ThrowsException() {

        TransferRequestDto requestDto = new TransferRequestDto();
        requestDto.setFromCardId(fromCard.getId());
        requestDto.setToCardId(99L);
        requestDto.setAmount(new BigDecimal("100.00"));

        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            transferService.transferFunds(user.getId(), requestDto);
        });

        assertEquals("Destination card not found with id: 99", exception.getMessage());
    }

    @Test
    void transferFunds_UserNotOwner_ThrowsException() {

        TransferRequestDto requestDto = new TransferRequestDto();
        requestDto.setFromCardId(fromCard.getId());
        requestDto.setToCardId(toCard.getId());
        requestDto.setAmount(new BigDecimal("100.00"));

        User otherUser = new User();
        otherUser.setId(2L);
        fromCard.setOwner(otherUser);

        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));


        SecurityException exception = assertThrows(SecurityException.class, () -> {
            transferService.transferFunds(user.getId(), requestDto);
        });

        assertEquals("User does not own one or both of the cards.", exception.getMessage());
    }

    @Test
    void transferFunds_SourceCardNotActive_ThrowsException() {
        fromCard.setStatus(CardStatus.BLOCKED);

        TransferRequestDto requestDto = new TransferRequestDto();
        requestDto.setFromCardId(fromCard.getId());
        requestDto.setToCardId(toCard.getId());
        requestDto.setAmount(new BigDecimal("100.00"));

        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));

        TransferException exception = assertThrows(TransferException.class, () -> {
            transferService.transferFunds(user.getId(), requestDto);
        });

        assertEquals("Both cards must be active for the transfer.", exception.getMessage());
    }

    @Test
    void transferFunds_DestinationCardNotActive_ThrowsException() {
        toCard.setStatus(CardStatus.BLOCKED);

        TransferRequestDto requestDto = new TransferRequestDto();
        requestDto.setFromCardId(fromCard.getId());
        requestDto.setToCardId(toCard.getId());
        requestDto.setAmount(new BigDecimal("100.00"));

        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));

        TransferException exception = assertThrows(TransferException.class, () -> {
            transferService.transferFunds(user.getId(), requestDto);
        });

        assertEquals("Both cards must be active for the transfer.", exception.getMessage());
    }

    @Test
    void transferFunds_SameCards_ThrowsException() {
        TransferRequestDto requestDto = new TransferRequestDto();
        requestDto.setFromCardId(fromCard.getId());
        requestDto.setToCardId(fromCard.getId());
        requestDto.setAmount(new BigDecimal("100.00"));


        TransferException exception = assertThrows(TransferException.class, () -> {
            transferService.transferFunds(user.getId(), requestDto);
        });

        assertEquals("Source and destination cards cannot be the same.", exception.getMessage());

        verify(cardRepository, never()).findById(anyLong());
    }
} 