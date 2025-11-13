package com.transport.ticket.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour un ticket
 * Renvoyé par tous les endpoints qui retournent un ticket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponse {

    private Long idTicket;

    private String ticketNumber;

    private Long idPassager;

    private Long idTrajet;

    private BigDecimal prix;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateAchat;

    private String statut; // "ACTIVE", "USED", "EXPIRED", "CANCELLED"

    private String qrCode; // Code QR en base64

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validFrom;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validUntil;

    // Informations additionnelles (calculées)
    private Boolean isExpired;

    private Boolean isValid;

    private Long remainingTimeInMinutes; // Temps restant avant expiration
}