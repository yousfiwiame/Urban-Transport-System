package com.transport.ticket.service;

import com.transport.ticket.dto.response.TransactionResponse;
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface du service Transaction
 */
public interface TransactionService {

    /**
     * Récupérer une transaction par son ID
     */
    TransactionResponse getTransactionById(Long transactionId);

    /**
     * Récupérer une transaction par sa référence
     */
    TransactionResponse getTransactionByReference(String transactionReference);

    /**
     * Récupérer toutes les transactions d'un ticket
     */
    List<TransactionResponse> getTicketTransactions(Long ticketId);

    /**
     * Récupérer toutes les transactions
     */
    List<TransactionResponse> getAllTransactions();

    /**
     * Récupérer les transactions par statut
     */
    List<TransactionResponse> getTransactionsByStatus(String status);

    /**
     * Calculer le revenu du jour
     */
    BigDecimal getTodayRevenue();

    /**
     * Calculer le revenu entre deux dates
     */
    BigDecimal getRevenueBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Obtenir les statistiques des transactions
     */
    Map<String, Object> getTransactionStatistics();
}