package com.transport.ticket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour mettre à jour le statut d'un ticket (admin)
 * Utilisé par : PATCH /api/tickets/{id}/status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTicketStatusRequest {

    @NotNull(message = "Le nouveau statut est obligatoire")
    private String newStatus; // "ACTIVE", "CANCELLED", "EXPIRED", "USED"

    private String reason; // Optionnel : raison du changement
}