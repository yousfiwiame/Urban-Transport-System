package com.transport.ticket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour valider un ticket (scan QR code)
 * Utilisé par : POST /api/tickets/validate
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateTicketRequest {

    @NotBlank(message = "Le numéro de ticket est obligatoire")
    private String ticketNumber;

    private String validationLocation; // Optionnel : "Station Hassan", "Arrêt Agdal"

    private Long validatorId; // Optionnel : ID du contrôleur qui valide
}