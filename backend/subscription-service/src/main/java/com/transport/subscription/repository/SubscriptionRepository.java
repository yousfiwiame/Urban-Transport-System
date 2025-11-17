package com.transport.subscription.repository;

import com.transport.subscription.entity.Subscription;
import com.transport.subscription.entity.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    List<Subscription> findByUserId(Integer userId);

    List<Subscription> findByUserIdAndStatus(Integer userId, SubscriptionStatus status);

    @Query("SELECT s FROM Subscription s WHERE s.status = :status " +
           "AND s.autoRenewEnabled = true " +
           "AND s.nextBillingDate = :nextBillingDate " +
           "AND s.deletedAt IS NULL")
    List<Subscription> findByStatusAndAutoRenewEnabledAndNextBillingDate(
            @Param("status") SubscriptionStatus status,
            @Param("nextBillingDate") LocalDate nextBillingDate
    );

    /**
     * Vérifie si un utilisateur a déjà un abonnement actif pour un plan donné
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM Subscription s WHERE s.userId = :userId " +
           "AND s.plan.planId = :planId " +
           "AND s.status = :status " +
           "AND s.deletedAt IS NULL")
    boolean existsByUserIdAndPlanIdAndStatusAndDeletedAtIsNull(
            @Param("userId") Integer userId,
            @Param("planId") Integer planId,
            @Param("status") SubscriptionStatus status
    );

    List<Subscription> findByPlan_PlanId(Integer planId);

    /**
     * Trouve les abonnements actifs d'un utilisateur.
     * Note: Cette méthode retourne les abonnements avec le statut ACTIVE.
     * Le service doit valider que l'endDate n'est pas passée pour garantir
     * qu'aucun abonnement expiré n'est retourné.
     */
    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId " +
           "AND s.status = :status " +
           "AND s.deletedAt IS NULL")
    List<Subscription> findActiveSubscriptionsByUserId(
            @Param("userId") Integer userId,
            @Param("status") SubscriptionStatus status
    );

    Optional<Subscription> findBySubscriptionIdAndDeletedAtIsNull(Integer subscriptionId);

    @Query("SELECT s FROM Subscription s WHERE s.status = :status " +
           "AND s.autoRenewEnabled = :autoRenewEnabled " +
           "AND s.deletedAt IS NULL")
    List<Subscription> findByStatusAndAutoRenewEnabled(
            @Param("status") SubscriptionStatus status,
            @Param("autoRenewEnabled") Boolean autoRenewEnabled
    );

    /**
     * Trouve les abonnements qui doivent être expirés (ACTIVE ou PAUSED avec endDate passée)
     */
    @Query("SELECT s FROM Subscription s WHERE " +
           "s.status IN ('ACTIVE', 'PAUSED') " +
           "AND s.endDate IS NOT NULL " +
           "AND s.endDate < :today " +
           "AND s.deletedAt IS NULL")
    List<Subscription> findSubscriptionsToExpire(@Param("today") LocalDate today);

    /**
     * Trouve les abonnements expirés avec un statut spécifique
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = :status " +
           "AND s.endDate < :today " +
           "AND s.deletedAt IS NULL")
    List<Subscription> findExpiredSubscriptions(
            @Param("status") SubscriptionStatus status,
            @Param("today") LocalDate today
    );

    @Query("SELECT s FROM Subscription s WHERE s.status = :status " +
           "AND s.nextBillingDate <= :date " +
           "AND s.deletedAt IS NULL")
    List<Subscription> findByStatusAndNextBillingDateLessThanEqual(
            @Param("status") SubscriptionStatus status,
            @Param("date") LocalDate date
    );

    List<Subscription> findByUserIdOrderByCreatedAtDesc(Integer userId);

    /**
     * Trouve les abonnements PENDING créés avant une date donnée (abandonnés).
     * Utilisé pour nettoyer les abonnements qui n'ont jamais été payés.
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = :status " +
           "AND s.createdAt < :thresholdDate " +
           "AND s.deletedAt IS NULL")
    List<Subscription> findAbandonedPendingSubscriptions(
            @Param("status") SubscriptionStatus status,
            @Param("thresholdDate") java.time.OffsetDateTime thresholdDate
    );
}

