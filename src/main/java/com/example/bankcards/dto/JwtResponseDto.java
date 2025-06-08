package com.example.bankcards.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponseDto {
    private String token;
    private final String type = "Bearer";

    public JwtResponseDto(String token) {
        this.token = token;
    }
} 