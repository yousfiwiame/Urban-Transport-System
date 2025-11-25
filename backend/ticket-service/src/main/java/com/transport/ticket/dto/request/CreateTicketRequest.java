package com.transport.ticket.dto.request;

import com.transport.ticket.model.TicketStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour créer un ticket (Admin uniquement)
 * Utilisé par : POST /api/tickets/admin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTicketRequest {

    @NotNull(message = "L'ID du passager est obligatoire")
    private Long idPassager;

    @NotNull(message = "L'ID du trajet est obligatoire")
    private Long idTrajet;

    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être positif")
    private BigDecimal prix;

    @NotNull(message = "La méthode de paiement est obligatoire")
    private String methodePaiement; // "CREDIT_CARD", "MOBILE_MONEY", "CASH"

    private TicketStatus statut; // Optionnel, par défaut ACTIVE

    private LocalDateTime dateAchat; // Optionnel, par défaut maintenant
    
    private LocalDateTime dateValidite; // Optionnel, calculé automatiquement si non fourni
}

