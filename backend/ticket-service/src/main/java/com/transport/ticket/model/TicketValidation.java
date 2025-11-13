package com.transport.ticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;  // ⬅️ AJOUTÉ
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "validations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder  // ⬅️ AJOUTÉ
public class TicketValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_validation")
    private Long idValidation;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @CreationTimestamp
    @Column(name = "date_validation", nullable = false, updatable = false)
    private LocalDate dateValidation;

    @Column(name = "heure_validation", nullable = false)
    private LocalTime heureValidation;

    @Column(name = "validation_timestamp")
    private LocalDateTime validationTimestamp;

    @Column(name = "validation_location", length = 255)
    private String validationLocation;

    @Column(name = "validator_id")
    private Long validatorId; // ID du contrôleur/valideur

    @PrePersist
    protected void onCreate() {
        if (heureValidation == null) {
            heureValidation = LocalTime.now();
        }
        if (validationTimestamp == null) {
            validationTimestamp = LocalDateTime.now();
        }
    }
}