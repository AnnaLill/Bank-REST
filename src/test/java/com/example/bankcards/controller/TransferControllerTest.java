package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtAuthenticationFilter;
import com.example.bankcards.security.JwtUtil;
import com.example.bankcards.security.SecurityUserDetails;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransferController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private SecurityUserDetails securityUserDetails;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRoles(Collections.singleton(Role.USER));

        securityUserDetails = new SecurityUserDetails(testUser);


        Authentication authentication = new UsernamePasswordAuthenticationToken(securityUserDetails, null, securityUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);


        when(userDetailsService.loadUserByUsername(any(String.class)))
                .thenReturn(securityUserDetails);


        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("mock_jwt_token");
    }

    @Test
    void transferFunds_ShouldReturnOkStatus() throws Exception {
        TransferRequestDto requestDto = new TransferRequestDto(
                1L, 2L, new BigDecimal("100.00"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(securityUserDetails, null, securityUserDetails.getAuthorities());

        doNothing().when(transferService).transferFunds(anyLong(), any(TransferRequestDto.class));

        mockMvc.perform(post("/api/v1/transfers")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(transferService, times(1)).transferFunds(eq(securityUserDetails.getId()), any(TransferRequestDto.class));
    }
}