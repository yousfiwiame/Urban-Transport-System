package com.transport.ticket.repository;

import com.transport.ticket.model.PaymentMethod;
import com.transport.ticket.model.PaymentStatus;
import com.transport.ticket.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // ==================== RECHERCHES DE BASE ====================

    Optional<Transaction> findByTransactionReference(String transactionReference);

    List<Transaction> findByTicketId(Long ticketId);

    boolean existsByTransactionReference(String transactionReference);

    // ==================== RECHERCHES PAR STATUT ====================

    List<Transaction> findByStatut(PaymentStatus statut);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.statut = 'SUCCESS'")
    long countSuccessfulTransactions();

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.statut = 'FAILED'")
    long countFailedTransactions();

    @Query("SELECT t.statut, COUNT(t) FROM Transaction t GROUP BY t.statut")
    List<Object[]> countTransactionsByStatus();

    // ==================== RECHERCHES PAR MÉTHODE DE PAIEMENT ====================

    List<Transaction> findByMethodePaiement(PaymentMethod methodePaiement);

    @Query("SELECT t.methodePaiement, COUNT(t) FROM Transaction t GROUP BY t.methodePaiement")
    List<Object[]> countTransactionsByPaymentMethod();

    // ==================== RECHERCHES PAR DATE ====================

    @Query("SELECT t FROM Transaction t WHERE t.dateTransaction BETWEEN :startDate AND :endDate")
    List<Transaction> findTransactionsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    List<Transaction> findTop10ByOrderByDateTransactionDesc();

    // ==================== STATISTIQUES FINANCIÈRES ====================

    /**
     * ✅ CORRIGÉ : Calculer le revenu total des transactions réussies aujourd'hui
     * Utilisé pour : Dashboard financier
     */
    @Query(value = "SELECT COALESCE(SUM(montant), 0) FROM transactions WHERE statut = 'SUCCESS' AND DATE(date_transaction) = CURRENT_DATE",
            nativeQuery = true)
    BigDecimal calculateTodayRevenue();

    /**
     * ✅ CORRIGÉ : Calculer le revenu entre deux dates
     * Utilisé pour : Rapports financiers
     */
    @Query(value = "SELECT COALESCE(SUM(montant), 0) FROM transactions WHERE statut = 'SUCCESS' AND date_transaction BETWEEN :startDate AND :endDate",
            nativeQuery = true)
    BigDecimal calculateRevenueBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Calculer le montant moyen des transactions
     */
    @Query("SELECT AVG(t.montant) FROM Transaction t WHERE t.statut = 'SUCCESS'")
    BigDecimal calculateAverageTransactionAmount();

    /**
     * Compter le nombre total de transactions
     */
    @Query("SELECT COUNT(t) FROM Transaction t")
    long countAllTransactions();

    /**
     * Calculer le revenu total (toutes transactions réussies)
     */
    @Query("SELECT COALESCE(SUM(t.montant), 0) FROM Transaction t WHERE t.statut = 'SUCCESS'")
    BigDecimal calculateTotalRevenue();
}