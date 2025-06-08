package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtUtil;
import com.example.bankcards.security.SecurityUserDetails;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private TransferService transferService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private SecurityUserDetails adminUserDetails;
    private SecurityUserDetails ordinaryUserDetails;

    @BeforeEach
    void setUp() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setPassword("adminpass");
        adminUser.setRoles(Collections.singleton(Role.ADMIN));
        adminUserDetails = new SecurityUserDetails(adminUser);

        User ordinaryUser = new User();
        ordinaryUser.setId(2L);
        ordinaryUser.setUsername("user");
        ordinaryUser.setPassword("userpass");
        ordinaryUser.setRoles(Collections.singleton(Role.USER));
        ordinaryUserDetails = new SecurityUserDetails(ordinaryUser);
    }

    @Test
    void getCardById_WhenCardExists_ShouldReturnCardDto() throws Exception {
        Long cardId = 1L;
        CardDto cardDto = new CardDto();
        cardDto.setId(cardId);
        cardDto.setBalance(new BigDecimal("1000.00"));
        cardDto.setMaskedCardNumber("**** **** **** 1234");
        given(cardService.getCardById(cardId)).willReturn(cardDto);

        mockMvc.perform(get("/api/v1/admin/cards/{id}", cardId)
                        .with(user(adminUserDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(cardId))
                .andExpect(jsonPath("$.balance").value(1000.00))
                .andExpect(jsonPath("$.maskedCardNumber").value("**** **** **** 1234"));
    }

    @Test
    void getCardById_WhenUserIsNotaAdmin_ShouldReturnForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/cards/{id}", 1L)
                        .with(user(ordinaryUserDetails)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
} 