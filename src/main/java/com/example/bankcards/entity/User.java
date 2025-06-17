package com.example.bankcards.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

/**
 * Сущность пользователя системы.
 * Представляет пользователя с учетными данными, ролями и статусом аккаунта.
 * 
 * @author Анна Тебенькова
 * @version 1.0
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"password"})
public class User {

    /**
     * Уникальный идентификатор пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Уникальное имя пользователя для входа в систему
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Зашифрованный пароль пользователя
     */
    @Column(nullable = false)
    private String password;

    /**
     * Роли пользователя в системе
     */
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    private Set<Role> roles;

    /**
     * Флаг, указывающий что аккаунт не истек
     */
    private boolean accountNonExpired = true;

    /**
     * Флаг, указывающий что аккаунт не заблокирован
     */
    private boolean accountNonLocked = true;

    /**
     * Флаг, указывающий что учетные данные не истекли
     */
    private boolean credentialsNonExpired = true;

    /**
     * Флаг, указывающий что аккаунт активен
     */
    private boolean enabled = true;
} 