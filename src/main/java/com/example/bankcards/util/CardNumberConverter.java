package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

/**
 * Конвертер для шифрования и дешифрования номеров банковских карт в базе данных.
 * Использует AES шифрование для защиты конфиденциальных данных номеров карт.
 * Автоматически шифрует номера при сохранении в БД и дешифрует при чтении.
 * 
 * @author Анна Тебенькова
 * @version 1.0
 */
@Converter
@Component
public class CardNumberConverter implements AttributeConverter<String, String> {

    /**
     * Алгоритм шифрования AES с режимом ECB и паддингом PKCS5
     */
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * Ключ шифрования, созданный из секретного ключа приложения
     */
    private final Key key;

    /**
     * Конструктор конвертера.
     * Создает ключ шифрования из секретного ключа приложения.
     * 
     * @param secret секретный ключ для шифрования (должен быть 16 символов)
     * @throws IllegalArgumentException если секретный ключ не соответствует требованиям
     */
    public CardNumberConverter(@Value("${app.encryption.secret}") String secret) {
        if (secret == null || secret.length() != 16) {
            throw new IllegalArgumentException("app.encryption.secret must be a 16-character string.");
        }
        this.key = new SecretKeySpec(secret.getBytes(), "AES");
    }

    /**
     * Шифрует номер карты для сохранения в базе данных.
     * 
     * @param attribute исходный номер карты
     * @return зашифрованный номер карты в формате Base64
     * @throws IllegalStateException если произошла ошибка шифрования
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt card number", e);
        }
    }

    /**
     * Дешифрует номер карты из базы данных.
     * 
     * @param dbData зашифрованный номер карты из БД
     * @return расшифрованный номер карты
     * @throws IllegalStateException если произошла ошибка дешифрования
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt card number", e);
        }
    }
} 