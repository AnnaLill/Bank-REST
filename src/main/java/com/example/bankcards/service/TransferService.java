package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;

public interface TransferService {
    void transferFunds(Long userId, TransferRequestDto requestDto);
} 