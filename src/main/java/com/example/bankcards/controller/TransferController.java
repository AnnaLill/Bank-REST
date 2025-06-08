package com.example.bankcards.controller;

import com.example.bankcards.dto.ErrorResponseDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.security.SecurityUserDetails;
import com.example.bankcards.service.TransferService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Fund Transfer Controller", description = "Endpoints for performing fund transfers")
public class TransferController {

    private final TransferService transferService;

    @Operation(summary = "Transfer funds between my cards", description = "Allows the authenticated user to transfer funds between two of their own cards.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer successful"),
            @ApiResponse(responseCode = "400", description = "Invalid transfer request (e.g., insufficient funds, same card, validation error)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not own one or both cards",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "One or both cards not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<Void> transferFunds(
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUserDetails userDetails,
            @Valid @RequestBody TransferRequestDto requestDto) {
        transferService.transferFunds(userDetails.getId(), requestDto);
        return ResponseEntity.ok().build();
    }
} 