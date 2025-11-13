package com.transport.ticket.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO de réponse après validation d'un ticket
 * Renvoyé par : POST /api/tickets/validate
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationResponse {

    private Long idValidation;

    private Long ticketId;

    private String ticketNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateValidation;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime heureValidation;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validationTimestamp;

    private String validationLocation;

    private Long validatorId;

    // Résultat de la validation
    private Boolean success;

    private String message; // "Ticket validé avec succès", "Ticket déjà utilisé", etc.

    // Informations du ticket
    private String passengerName; // Optionnel

    private String routeInfo; // Optionnel
}