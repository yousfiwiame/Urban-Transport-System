package com.transport.ticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;  // ⬅️ AJOUTÉ
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder  // ⬅️ AJOUTÉ
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ticket")
    private Long idTicket;

    @Column(name = "id_passager", nullable = false)
    private Long idPassager;

    @Column(name = "id_trajet", nullable = false)
    private Long idTrajet;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal prix;

    @CreationTimestamp
    @Column(name = "date_achat", nullable = false, updatable = false)
    private LocalDateTime dateAchat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TicketStatus statut;

    @Column(name = "ticket_number", unique = true, length = 100)
    private String ticketNumber;

    @Lob
    @Column(name = "qr_code", columnDefinition = "TEXT")
    private String qrCode;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @PrePersist
    protected void onCreate() {
        if (statut == null) {
            statut = TicketStatus.ACTIVE;
        }
        if (ticketNumber == null) {
            ticketNumber = "TKT-" + System.currentTimeMillis();
        }
        if (validFrom == null) {
            validFrom = LocalDateTime.now();
        }
        if (validUntil == null) {
            validUntil = LocalDateTime.now().plusHours(24); // Valide 24h
        }
    }
}