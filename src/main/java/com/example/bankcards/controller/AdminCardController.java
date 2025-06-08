package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.ErrorResponseDto;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/cards")
@RequiredArgsConstructor
@Tag(name = "Admin Card Controller", description = "Endpoints for admin to manage cards")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminCardController {

    private final CardService cardService;

    @Operation(summary = "Get all cards", description = "Retrieves a paginated list of all cards in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of cards"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<Page<CardDto>> getAllCards(Pageable pageable) {
        return ResponseEntity.ok(cardService.getAllCards(pageable));
    }

    @Operation(summary = "Get card by ID", description = "Retrieves a single card by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the card"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Card not found with the given ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCardById(@Parameter(description = "ID of the card to be retrieved") @PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }

    @Operation(summary = "Create a new card", description = "Creates a new bank card for a specified user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid card data provided (validation error)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found for the given userId",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CardCreateRequestDto requestDto) {
        CardDto createdCard = cardService.createCard(requestDto);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }

    @Operation(summary = "Update card status", description = "Updates the status of an existing card (e.g., ACTIVE, BLOCKED).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status value provided",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Card not found with the given ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<CardDto> updateCardStatus(@Parameter(description = "ID of the card to update") @PathVariable Long id,
                                                    @Parameter(description = "The new status for the card (e.g., ACTIVE, BLOCKED, EXPIRED)") @RequestParam String status) {
        CardDto updatedCard = cardService.updateCardStatus(id, status);
        return ResponseEntity.ok(updatedCard);
    }

    @Operation(summary = "Delete a card", description = "Deletes a card from the system by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Card not found with the given ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@Parameter(description = "ID of the card to be deleted") @PathVariable Long id) {
        cardService.deleteCard(id);
    }
} 