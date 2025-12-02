package com.transport.subscription.repository;

import com.transport.subscription.entity.Subscription;
import com.transport.subscription.entity.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    List<Subscription> findByUserId(UUID userId);

    List<Subscription> findByUserIdAndStatus(UUID userId, SubscriptionStatus status);

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
            @Param("userId") UUID userId,
            @Param("planId") UUID planId,
            @Param("status") SubscriptionStatus status
    );

    List<Subscription> findByPlan_PlanId(UUID planId);

    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId " +
           "AND s.status = :status " +
           "AND s.deletedAt IS NULL")
    List<Subscription> findActiveSubscriptionsByUserId(
            @Param("userId") UUID userId,
            @Param("status") SubscriptionStatus status
    );

    Optional<Subscription> findBySubscriptionIdAndDeletedAtIsNull(UUID subscriptionId);

    @Query("SELECT s FROM Subscription s WHERE s.status = :status " +
           "AND s.autoRenewEnabled = :autoRenewEnabled " +
           "AND s.deletedAt IS NULL")
    List<Subscription> findByStatusAndAutoRenewEnabled(
            @Param("status") SubscriptionStatus status,
            @Param("autoRenewEnabled") Boolean autoRenewEnabled
    );

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

    List<Subscription> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Counts active subscriptions.
     * Used for admin dashboard statistics.
     *
     * @return the number of active subscriptions
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = 'ACTIVE' AND s.deletedAt IS NULL")
    long countByActiveTrue();

    /**
     * Finds abandoned PENDING subscriptions created before the threshold date.
     * Used by cleanup scheduler to cancel old pending subscriptions.
     *
     * @param status the subscription status to filter by
     * @param thresholdDate the date threshold
     * @return list of abandoned subscriptions
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = :status " +
           "AND s.createdAt < :thresholdDate " +
           "AND s.deletedAt IS NULL")
    List<Subscription> findAbandonedPendingSubscriptions(
            @Param("status") SubscriptionStatus status,
            @Param("thresholdDate") OffsetDateTime thresholdDate
    );

    /**
     * Finds subscriptions that should be expired.
     * Looks for ACTIVE or PAUSED subscriptions whose end date has passed.
     * Used by expiration scheduler.
     *
     * @param today the current date
     * @return list of subscriptions to expire
     */
    @Query("SELECT s FROM Subscription s WHERE s.status IN ('ACTIVE', 'PAUSED') " +
           "AND s.endDate < :today " +
           "AND s.deletedAt IS NULL")
    List<Subscription> findSubscriptionsToExpire(@Param("today") LocalDate today);
}

