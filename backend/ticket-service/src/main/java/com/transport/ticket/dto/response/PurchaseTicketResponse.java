package com.transport.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse après l'achat d'un ticket
 * Renvoyé par : POST /api/tickets/purchase
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseTicketResponse {

    private TicketResponse ticket;

    private TransactionResponse transaction;

    private String message; // "Ticket acheté avec succès"

    private Boolean success;
}