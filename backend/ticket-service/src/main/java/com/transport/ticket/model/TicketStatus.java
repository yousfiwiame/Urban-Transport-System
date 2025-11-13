package com.transport.ticket.model;

public enum TicketStatus {
    ACTIVE,      // Ticket actif, non utilisé
    USED,        // Ticket utilisé/validé
    EXPIRED,     // Ticket expiré
    CANCELLED    // Ticket annulé/remboursé
}