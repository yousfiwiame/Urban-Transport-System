package com.transport.ticket.service.impl;

import com.transport.ticket.dto.mapper.TransactionMapper;
import com.transport.ticket.dto.response.TransactionResponse;
import com.transport.ticket.exception.TransactionNotFoundException;
import com.transport.ticket.model.PaymentStatus;
import com.transport.ticket.model.Transaction;
import com.transport.ticket.repository.TransactionRepository;
import com.transport.ticket.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implémentation du service Transaction
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public TransactionResponse getTransactionById(Long transactionId) {
        log.info("🔍 Recherche de la transaction avec ID: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.error("❌ Transaction non trouvée avec ID: {}", transactionId);
                    return new TransactionNotFoundException("Transaction non trouvée avec l'ID: " + transactionId);
                });

        log.info("✅ Transaction trouvée: {}", transaction.getTransactionReference());
        return transactionMapper.toResponse(transaction);
    }

    @Override
    public TransactionResponse getTransactionByReference(String transactionReference) {
        log.info("🔍 Recherche de la transaction avec référence: {}", transactionReference);

        Transaction transaction = transactionRepository.findByTransactionReference(transactionReference)
                .orElseThrow(() -> {
                    log.error("❌ Transaction non trouvée avec référence: {}", transactionReference);
                    return new TransactionNotFoundException("Transaction non trouvée avec la référence: " + transactionReference);
                });

        log.info("✅ Transaction trouvée - ID: {}", transaction.getIdTransaction());
        return transactionMapper.toResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getTicketTransactions(Long ticketId) {
        log.info("🔍 Recherche des transactions pour le ticket ID: {}", ticketId);

        List<Transaction> transactions = transactionRepository.findByTicketId(ticketId);

        log.info("✅ {} transaction(s) trouvée(s) pour le ticket ID: {}", transactions.size(), ticketId);

        return transactions.stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getAllTransactions() {
        log.info("🔍 Récupération de toutes les transactions");

        List<Transaction> transactions = transactionRepository.findAll();

        log.info("✅ {} transaction(s) trouvée(s)", transactions.size());

        return transactions.stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getTransactionsByStatus(String status) {
        log.info("🔍 Recherche des transactions avec statut: {}", status);

        PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
        List<Transaction> transactions = transactionRepository.findByStatut(paymentStatus);

        log.info("✅ {} transaction(s) trouvée(s) avec statut: {}", transactions.size(), status);

        return transactions.stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTodayRevenue() {
        log.info("💰 Calcul du revenu du jour");

        BigDecimal revenue = transactionRepository.calculateTodayRevenue();

        log.info("✅ Revenu du jour: {} MAD", revenue);

        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getRevenueBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("💰 Calcul du revenu entre {} et {}", startDate, endDate);

        BigDecimal revenue = transactionRepository.calculateRevenueBetweenDates(startDate, endDate);

        log.info("✅ Revenu pour la période: {} MAD", revenue);

        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public Map<String, Object> getTransactionStatistics() {
        log.info("📊 Calcul des statistiques des transactions");

        Map<String, Object> stats = new HashMap<>();

        // Nombre total de transactions
        long totalTransactions = transactionRepository.count();
        stats.put("totalTransactions", totalTransactions);

        // Nombre de transactions réussies
        long successfulTransactions = transactionRepository.countSuccessfulTransactions();
        stats.put("successfulTransactions", successfulTransactions);

        // Nombre de transactions échouées
        long failedTransactions = transactionRepository.countFailedTransactions();
        stats.put("failedTransactions", failedTransactions);

        // Taux de réussite
        double successRate = totalTransactions > 0
                ? (successfulTransactions * 100.0) / totalTransactions
                : 0.0;
        stats.put("successRate", String.format("%.2f%%", successRate));

        // Revenu du jour
        BigDecimal todayRevenue = getTodayRevenue();
        stats.put("todayRevenue", todayRevenue);

        // Montant moyen des transactions
        BigDecimal averageAmount = transactionRepository.calculateAverageTransactionAmount();
        stats.put("averageTransactionAmount", averageAmount != null ? averageAmount : BigDecimal.ZERO);

        // Transactions par statut
        List<Object[]> statusCounts = transactionRepository.countTransactionsByStatus();
        Map<String, Long> transactionsByStatus = new HashMap<>();
        for (Object[] result : statusCounts) {
            transactionsByStatus.put(result[0].toString(), (Long) result[1]);
        }
        stats.put("transactionsByStatus", transactionsByStatus);

        // Transactions par méthode de paiement
        List<Object[]> paymentMethodCounts = transactionRepository.countTransactionsByPaymentMethod();
        Map<String, Long> transactionsByPaymentMethod = new HashMap<>();
        for (Object[] result : paymentMethodCounts) {
            transactionsByPaymentMethod.put(result[0].toString(), (Long) result[1]);
        }
        stats.put("transactionsByPaymentMethod", transactionsByPaymentMethod);

        log.info("✅ Statistiques calculées avec succès");

        return stats;
    }
}