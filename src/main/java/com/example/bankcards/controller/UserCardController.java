package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.security.SecurityUserDetails;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserCardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<Page<CardDto>> getMyCards(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            Pageable pageable) {
        Page<CardDto> cards = cardService.getCardsByUserId(userDetails.getId(), pageable);
        return ResponseEntity.ok(cards);
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<CardDto> blockMyCard(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @PathVariable Long id) {
        CardDto updatedCard = cardService.requestCardBlock(id, userDetails.getId());
        return ResponseEntity.ok(updatedCard);
    }

    @PostMapping("/transfers")
    public ResponseEntity<Void> transferFunds(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @Valid @RequestBody TransferRequestDto requestDto) {
        cardService.transferFunds(userDetails.getId(), requestDto);
        return ResponseEntity.ok().build();
    }
} 