package com.transport.ticket.dto.request;

import com.transport.ticket.model.TicketStatus;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour modifier un ticket (Admin uniquement)
 * Utilisé par : PUT /api/tickets/admin/{id}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTicketRequest {

    private Long idPassager;

    private Long idTrajet;

    @Positive(message = "Le prix doit être positif")
    private BigDecimal prix;

    private String methodePaiement;

    private TicketStatus statut;

    private LocalDateTime dateValidite;
    
    private String remarque;
}

