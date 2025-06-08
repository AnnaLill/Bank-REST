package com.example.bankcards.controller;

import com.example.bankcards.dto.JwtRequestDto;
import com.example.bankcards.dto.UserRegistrationDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtAuthenticationFilter;
import com.example.bankcards.security.JwtUtil;
import com.example.bankcards.security.SecurityUserDetails;
import com.example.bankcards.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private SecurityUserDetails mockUserDetails;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("testpass");
        testUser.setRoles(Collections.singleton(Role.USER));
        mockUserDetails = new SecurityUserDetails(testUser);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUserDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userDetailsService.loadUserByUsername(any(String.class)))
                .thenReturn(mockUserDetails);

        when(jwtUtil.generateToken(any(org.springframework.security.core.userdetails.UserDetails.class))).thenReturn("mock_jwt_token");
    }

    @Test
    void login_ShouldReturnJwtToken() throws Exception {
        JwtRequestDto loginRequest = new JwtRequestDto("testuser", "testpass");
        String token = "mock_jwt_token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(mockUserDetails, null, mockUserDetails.getAuthorities()));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    void registerUser_ShouldReturnSuccess() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto("newuser", "newpass");
        when(userDetailsService.registerNewUser(any(UserRegistrationDto.class)))
                .thenReturn("User registered successfully!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }
}