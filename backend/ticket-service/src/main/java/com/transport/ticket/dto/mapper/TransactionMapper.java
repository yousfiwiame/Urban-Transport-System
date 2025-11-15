package com.transport.ticket.dto.mapper;

import com.transport.ticket.dto.response.TransactionResponse;
import com.transport.ticket.model.Transaction;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir Transaction (Entity) ↔️ TransactionResponse (DTO)
 */
@Component
public class TransactionMapper {

    /**
     * Convertir Transaction → TransactionResponse
     */
    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        String message = getStatusMessage(transaction.getStatut().name());

        return TransactionResponse.builder()
                .idTransaction(transaction.getIdTransaction())
                .transactionReference(transaction.getTransactionReference())
                .ticketId(transaction.getTicketId())
                .montant(transaction.getMontant())
                .dateTransaction(transaction.getDateTransaction())
                .statut(transaction.getStatut().name())
                .methodePaiement(transaction.getMethodePaiement().name())
                .description(transaction.getDescription())
                .message(message)
                .build();
    }

    /**
     * Générer un message selon le statut
     */
    private String getStatusMessage(String status) {
        return switch (status) {
            case "SUCCESS" -> "Paiement réussi";
            case "FAILED" -> "Échec du paiement";
            case "PENDING" -> "Paiement en cours de traitement";
            case "REFUNDED" -> "Paiement remboursé";
            default -> "Statut inconnu";
        };
    }

    /**
     * Convertir TransactionResponse → Transaction
     */
    public Transaction toEntity(TransactionResponse response) {
        if (response == null) {
            return null;
        }

        Transaction transaction = new Transaction();
        transaction.setIdTransaction(response.getIdTransaction());
        transaction.setTransactionReference(response.getTransactionReference());
        transaction.setTicketId(response.getTicketId());
        transaction.setMontant(response.getMontant());
        transaction.setDateTransaction(response.getDateTransaction());
        transaction.setDescription(response.getDescription());

        return transaction;
    }
}