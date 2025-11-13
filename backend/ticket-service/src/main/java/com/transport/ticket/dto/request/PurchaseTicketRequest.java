package com.transport.ticket.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour acheter un ticket
 * Utilisé par : POST /api/tickets/purchase
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseTicketRequest {

    @NotNull(message = "L'ID du passager est obligatoire")
    private Long idPassager;

    @NotNull(message = "L'ID du trajet est obligatoire")
    private Long idTrajet;

    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être positif")
    private BigDecimal prix;

    @NotNull(message = "La méthode de paiement est obligatoire")
    private String methodePaiement; // "CREDIT_CARD", "MOBILE_MONEY", "CASH"
}