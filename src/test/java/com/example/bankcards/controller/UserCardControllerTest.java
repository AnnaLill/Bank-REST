package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.RestPageImpl;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.SecurityUserDetails;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private TransferService transferService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long userId = 1L;
    private SecurityUserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRoles(Collections.singleton(Role.USER));
        mockUserDetails = new SecurityUserDetails(testUser);


    }

    @Test
    void getUserCards_ShouldReturnListOfCards() throws Exception {
        List<CardDto> cards = Arrays.asList(
                new CardDto(101L, "**** **** **** 1111", new BigDecimal("100.00"), CardStatus.ACTIVE),
                new CardDto(102L, "**** **** **** 2222", new BigDecimal("200.00"), CardStatus.BLOCKED)
        );
        int page = 0;
        int size = 10;
        long totalElements = cards.size();
        Pageable pageable = PageRequest.of(page, size);
        Page<CardDto> cardPage = new PageImpl<>(cards, pageable, totalElements);
        given(cardService.getCardsByUserId(eq(userId), any(Pageable.class)))
                .willReturn(cardPage);

        mockMvc.perform(get("/api/v1/user/cards")
                        .with(user(mockUserDetails))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(cardService, times(1)).getCardsByUserId(eq(userId), any(Pageable.class));
    }

    @Test
    void blockUserCard_ShouldReturnNoContent() throws Exception {
        Long cardId = 1L;
        CardDto blockedCardDto = new CardDto();
        blockedCardDto.setId(cardId);
        blockedCardDto.setMaskedCardNumber("**** **** **** 1234");
        blockedCardDto.setBalance(new BigDecimal("0.00"));

        given(cardService.requestCardBlock(cardId, userId)).willReturn(blockedCardDto);

        mockMvc.perform(patch("/api/v1/user/cards/{id}/block", cardId)
                        .with(user(mockUserDetails)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(cardService, times(1)).requestCardBlock(cardId, userId);
    }
}