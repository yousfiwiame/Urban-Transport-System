package com.transport.ticket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour demander un remboursement
 * Utilisé par : POST /api/tickets/{id}/refund
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundTicketRequest {

    @NotNull(message = "L'ID du ticket est obligatoire")
    private Long ticketId;

    @NotBlank(message = "La raison du remboursement est obligatoire")
    private String reason; // "Bus en retard", "Annulation du voyage", etc.

    private String additionalNotes; // Optionnel
}