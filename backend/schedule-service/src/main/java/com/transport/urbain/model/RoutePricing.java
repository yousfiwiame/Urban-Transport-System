package com.transport.urbain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité pour gérer les prix des routes
 */
@Entity
@Table(name = "route_pricing")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutePricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_id", nullable = false)
    private Long routeId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice; // Prix de base en MAD

    @Column(precision = 10, scale = 2)
    private BigDecimal peakHourPrice; // Prix en heure de pointe (optionnel)

    @Column(precision = 10, scale = 2)
    private BigDecimal weekendPrice; // Prix weekend (optionnel)

    @Column(length = 20)
    private String currency = "MAD"; // Devise

    @Column(nullable = false)
    private Boolean active = true; // Si ce pricing est actif

    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom; // Date de début de validité

    @Column(name = "effective_until")
    private LocalDateTime effectiveUntil; // Date de fin de validité

    @Column(length = 500)
    private String description; // Description optionnelle

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (currency == null) {
            currency = "MAD";
        }
        if (active == null) {
            active = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

