package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    private User testUser;
    private Card testCard;
    private CardDto testCardDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testCard = new Card();
        testCard.setId(1L);
        testCard.setCardNumber("1234567890123456");
        testCard.setOwner(testUser);
        testCard.setExpiryDate(YearMonth.of(2025, 12));
        testCard.setBalance(new BigDecimal("1000.00"));
        testCard.setStatus(CardStatus.ACTIVE);

        testCardDto = new CardDto();
        testCardDto.setId(1L);
        testCardDto.setMaskedCardNumber("**** **** **** 3456");
        testCardDto.setOwnerUsername("testuser");
        testCardDto.setExpiryDate("12/25");
        testCardDto.setBalance(new BigDecimal("1000.00"));
        testCardDto.setStatus(CardStatus.ACTIVE);
    }

    @Test
    void getAllCards_ShouldReturnPageOfCardDtos() {
        Pageable pageable = Pageable.unpaged();
        Page<Card> cardsPage = new PageImpl<>(Collections.singletonList(testCard), pageable, 1);
        when(cardRepository.findAll(pageable)).thenReturn(cardsPage);

        Page<CardDto> result = cardService.getAllCards(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testCardDto.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getCardById_WhenCardExists_ShouldReturnCardDto() {
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(testCard));

        CardDto result = cardService.getCardById(1L);

        assertNotNull(result);
        assertEquals(testCardDto.getId(), result.getId());
        assertEquals(testCardDto.getMaskedCardNumber(), result.getMaskedCardNumber());
    }

    @Test
    void getCardById_WhenCardNotFound_ShouldThrowResourceNotFoundException() {
        when(cardRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            cardService.getCardById(99L);
        });

        assertEquals("Card not found with id: 99", exception.getMessage());
    }

    @Test
    void createCard_WhenValidRequest_ShouldReturnCardDto() {
        CardCreateRequestDto requestDto = new CardCreateRequestDto(
                1L, "1111222233334444", YearMonth.of(2026, 1), new BigDecimal("500.00"));

        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        CardDto result = cardService.createCard(requestDto);

        assertNotNull(result);
        assertEquals(testCardDto.getId(), result.getId());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void createCard_WhenCardNumberExists_ShouldThrowIllegalArgumentException() {
        CardCreateRequestDto requestDto = new CardCreateRequestDto(
                1L, "1234567890123456", YearMonth.of(2026, 1), new BigDecimal("500.00"));

        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(testCard));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cardService.createCard(requestDto);
        });

        assertEquals("Card with this number already exists.", exception.getMessage());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void createCard_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        CardCreateRequestDto requestDto = new CardCreateRequestDto(
                99L, "1111222233334444", YearMonth.of(2026, 1), new BigDecimal("500.00"));

        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            cardService.createCard(requestDto);
        });

        assertEquals("User not found with id: 99", exception.getMessage());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void updateCardStatus_WhenValidStatus_ShouldReturnUpdatedCardDto() {
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        CardDto result = cardService.updateCardStatus(1L, "BLOCKED");

        assertNotNull(result);
        assertEquals(CardStatus.BLOCKED, result.getStatus());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void updateCardStatus_WhenCardNotFound_ShouldThrowResourceNotFoundException() {
        when(cardRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            cardService.updateCardStatus(99L, "BLOCKED");
        });

        assertEquals("Card not found with id: 99", exception.getMessage());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void updateCardStatus_WhenInvalidStatus_ShouldThrowIllegalArgumentException() {
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(testCard));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cardService.updateCardStatus(1L, "INVALID_STATUS");
        });

        assertEquals("Invalid status value: INVALID_STATUS", exception.getMessage());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void deleteCard_WhenCardExists_ShouldDeleteCard() {
        when(cardRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(cardRepository).deleteById(anyLong());

        cardService.deleteCard(1L);

        verify(cardRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCard_WhenCardNotFound_ShouldThrowResourceNotFoundException() {
        when(cardRepository.existsById(anyLong())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            cardService.deleteCard(99L);
        });

        assertEquals("Card not found with id: 99", exception.getMessage());
        verify(cardRepository, never()).deleteById(anyLong());
    }

    @Test
    void getCardsByUserId_WhenUserExists_ShouldReturnPageOfCardDtos() {
        Pageable pageable = Pageable.unpaged();
        Page<Card> cardsPage = new PageImpl<>(Collections.singletonList(testCard), pageable, 1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cardRepository.findByOwner(any(User.class), eq(pageable))).thenReturn(cardsPage);

        Page<CardDto> result = cardService.getCardsByUserId(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testCardDto.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getCardsByUserId_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        Pageable pageable = Pageable.unpaged();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            cardService.getCardsByUserId(99L, pageable);
        });

        assertEquals("User not found with id: 99", exception.getMessage());
        verify(cardRepository, never()).findByOwner(any(User.class), any(Pageable.class));
    }

    @Test
    void requestCardBlock_WhenUserOwnsCard_ShouldBlockCard() {
        testCard.setOwner(testUser); // Ensure the test card is owned by testUser
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        CardDto result = cardService.requestCardBlock(1L, testUser.getId());

        assertNotNull(result);
        assertEquals(CardStatus.BLOCKED, result.getStatus());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void requestCardBlock_WhenCardNotFound_ShouldThrowResourceNotFoundException() {
        when(cardRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            cardService.requestCardBlock(99L, 1L);
        });

        assertEquals("Card not found with id: 99", exception.getMessage());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void requestCardBlock_WhenUserDoesNotOwnCard_ShouldThrowSecurityException() {
        User otherUser = new User();
        otherUser.setId(2L);
        testCard.setOwner(new User() {{
            setId(3L);
        }});

        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(testCard));

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            cardService.requestCardBlock(1L, otherUser.getId());
        });

        assertEquals("User 2 does not have permission to block card 1", exception.getMessage());
        verify(cardRepository, never()).save(any(Card.class));
    }
} 