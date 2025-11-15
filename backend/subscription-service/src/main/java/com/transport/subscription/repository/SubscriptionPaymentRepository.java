package com.transport.subscription.repository;

import com.transport.subscription.entity.SubscriptionPayment;
import com.transport.subscription.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionPaymentRepository extends JpaRepository<SubscriptionPayment, UUID> {

    List<SubscriptionPayment> findBySubscription_SubscriptionId(UUID subscriptionId);

    List<SubscriptionPayment> findByPaymentStatus(PaymentStatus paymentStatus);

    Optional<SubscriptionPayment> findByExternalTxnId(String externalTxnId);

    boolean existsByIdempotencyKey(String idempotencyKey);

    Optional<SubscriptionPayment> findByIdempotencyKey(String idempotencyKey);

    @Query("SELECT p FROM SubscriptionPayment p " +
           "WHERE p.subscription.subscriptionId = :subscriptionId " +
           "ORDER BY p.paymentDate DESC")
    List<SubscriptionPayment> findBySubscriptionIdOrderByPaymentDateDesc(
            @Param("subscriptionId") UUID subscriptionId
    );

    @Query("SELECT COALESCE(SUM(p.amount), 0.0) FROM SubscriptionPayment p " +
           "WHERE p.subscription.subscriptionId = :subscriptionId " +
           "AND p.paymentStatus = :status")
    Double calculateTotalPaidAmount(
            @Param("subscriptionId") UUID subscriptionId,
            @Param("status") PaymentStatus status
    );
}

