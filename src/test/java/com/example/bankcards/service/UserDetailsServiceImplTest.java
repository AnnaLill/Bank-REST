package com.example.bankcards.service;

import com.example.bankcards.dto.UserRegistrationDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedpassword");
        testUser.setRoles(Collections.singleton(Role.USER));
        testUser.setAccountNonExpired(true);
        testUser.setAccountNonLocked(true);
        testUser.setCredentialsNonExpired(true);
        testUser.setEnabled(true);
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedpassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistentuser");
        });
    }

    @Test
    void registerNewUser_ShouldSaveUserAndReturnSuccessMessage() {
        UserRegistrationDto registrationDto = new UserRegistrationDto("newuser", "rawpassword");
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("encodedRawPassword");
        newUser.setRoles(Set.of(Role.USER));
        newUser.setAccountNonExpired(true);
        newUser.setAccountNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        newUser.setEnabled(true);

        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());
        given(passwordEncoder.encode(registrationDto.getPassword())).willReturn("encodedRawPassword");
        given(userRepository.save(any(User.class))).willReturn(newUser);

        String result = userDetailsService.registerNewUser(registrationDto);

        assertEquals("User registered successfully!", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerNewUser_WhenUserAlreadyExists_ShouldThrowIllegalStateException() {
        UserRegistrationDto registrationDto = new UserRegistrationDto("existinguser", "password");

        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(new User()));

        assertThrows(IllegalStateException.class, () -> {
            userDetailsService.registerNewUser(registrationDto);
        });
        verify(userRepository, never()).save(any(User.class));
    }
} 