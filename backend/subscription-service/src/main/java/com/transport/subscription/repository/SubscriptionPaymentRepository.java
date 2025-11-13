package com.transport.subscription.repository;

import com.transport.subscription.entity.SubscriptionPayment;
import com.transport.subscription.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
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
}

