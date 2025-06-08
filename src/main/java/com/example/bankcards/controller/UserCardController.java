package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.ErrorResponseDto;
import com.example.bankcards.security.SecurityUserDetails;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Card Controller", description = "Endpoints for user-specific card operations")
public class UserCardController {

    private final CardService cardService;

    @Operation(summary = "Get all cards for the authenticated user", description = "Returns a paginated list of cards belonging to the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of cards",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CardDto.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CardDto>> getMyCards(
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUserDetails userDetails,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardDto> cards = cardService.getCardsByUserId(userDetails.getId(), pageable);
        return ResponseEntity.ok(cards);
    }

    @Operation(summary = "Block my card", description = "Allows the authenticated user to block one of their own cards.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card successfully blocked"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not own this card",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Card not found with the given ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PatchMapping("/{id}/block")
    public ResponseEntity<Void> blockMyCard(
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUserDetails userDetails,
            @Parameter(description = "ID of the card to be blocked") @PathVariable Long id) {
        cardService.requestCardBlock(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
} 