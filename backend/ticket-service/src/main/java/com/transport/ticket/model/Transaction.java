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
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder  // ⬅️ AJOUTÉ
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private Long idTransaction;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @CreationTimestamp
    @Column(name = "date_transaction", nullable = false, updatable = false)
    private LocalDateTime dateTransaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PaymentStatus statut;

    @Enumerated(EnumType.STRING)
    @Column(name = "methode_paiement", nullable = false, length = 50)
    private PaymentMethod methodePaiement;

    @Column(name = "transaction_reference", unique = true, length = 100)
    private String transactionReference;

    @Column(name = "description", length = 255)
    private String description;

    @PrePersist
    protected void onCreate() {
        if (statut == null) {
            statut = PaymentStatus.PENDING;
        }
        if (transactionReference == null) {
            transactionReference = "TXN-" + System.currentTimeMillis();
        }
    }
}