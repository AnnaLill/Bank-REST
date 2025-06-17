package com.example.bankcards.service;

import com.example.bankcards.dto.UserRegistrationDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.SecurityUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Реализация сервиса для работы с пользователями Spring Security.
 * Предоставляет функциональность для загрузки пользователей по имени
 * и регистрации новых пользователей в системе.
 * 
 * @author Анна Тебенькова
 * @version 1.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор сервиса.
     * 
     * @param userRepository репозиторий для работы с пользователями
     * @param passwordEncoder кодировщик паролей
     */
    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Загружает пользователя по имени пользователя для Spring Security.
     * 
     * @param username имя пользователя
     * @return детали пользователя для Spring Security
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return new SecurityUserDetails(user);
    }

    /**
     * Регистрирует нового пользователя в системе.
     * Создает учетную запись с ролью USER по умолчанию.
     * 
     * @param registrationDto данные для регистрации пользователя
     * @return сообщение об успешной регистрации
     * @throws IllegalStateException если пользователь с таким именем уже существует
     */
    @Transactional
    public String registerNewUser(UserRegistrationDto registrationDto) {
        if (userRepository.findByUsername(registrationDto.getUsername()).isPresent()) {
            throw new IllegalStateException("User with this username already exists!");
        }

        User newUser = new User();
        newUser.setUsername(registrationDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        newUser.setRoles(Collections.singleton(Role.USER));
        newUser.setAccountNonExpired(true);
        newUser.setAccountNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        newUser.setEnabled(true);

        userRepository.save(newUser);
        return "User registered successfully!";
    }
} 