package com.transport.ticket.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour une transaction
 * Renvoyé par les endpoints de paiement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long idTransaction;

    private String transactionReference;

    private Long ticketId;

    private BigDecimal montant;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTransaction;

    private String statut; // "SUCCESS", "FAILED", "PENDING", "REFUNDED"

    private String methodePaiement; // "CREDIT_CARD", "MOBILE_MONEY", "CASH"

    private String description;

    // Informations additionnelles
    private String message; // "Paiement réussi", "Erreur de paiement", etc.
}