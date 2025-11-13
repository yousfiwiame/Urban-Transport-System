package com.transport.ticket.model;

public enum PaymentStatus {
    PENDING,     // En attente
    COMPLETED,   // Paiement complété
    FAILED,      // Paiement échoué
    REFUNDED     // Remboursé
}