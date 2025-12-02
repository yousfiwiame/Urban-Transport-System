package com.transport.subscription.scheduler;

import com.transport.subscription.entity.Subscription;
import com.transport.subscription.entity.SubscriptionPayment;
import com.transport.subscription.entity.enums.PaymentStatus;
import com.transport.subscription.entity.enums.PaymentType;
import com.transport.subscription.entity.enums.SubscriptionStatus;
import com.transport.subscription.repository.SubscriptionRepository;
import com.transport.subscription.service.PaymentGateway;
import com.transport.subscription.service.PaymentResult;
import com.transport.subscription.util.DateUtil;
import com.transport.subscription.util.PriceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionRenewalScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentGateway paymentGateway;

    @Value("${scheduler.renewal.enabled:true}")
    private boolean renewalEnabled;

    @Scheduled(cron = "${scheduler.renewal.cron:0 0 0 * * *}") // Daily at midnight
    @Transactional
    public void processRenewals() {
        if (!renewalEnabled) {
            log.debug("Renewal scheduler is disabled");
            return;
        }

        log.info("Starting subscription renewal process");

        LocalDate today = LocalDate.now();
        List<Subscription> subscriptionsToRenew = subscriptionRepository
                .findByStatusAndAutoRenewEnabledAndNextBillingDate(
                        SubscriptionStatus.ACTIVE,
                        today
                );

        log.info("Found {} subscriptions to renew", subscriptionsToRenew.size());

        int successCount = 0;
        int failureCount = 0;

        for (Subscription subscription : subscriptionsToRenew) {
            try {
                processRenewal(subscription);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                log.error("Failed to renew subscription {}: {}", 
                        subscription.getSubscriptionId(), e.getMessage(), e);
                // Mark subscription as failed but continue with others
                handleRenewalFailure(subscription, e.getMessage());
            }
        }

        log.info("Renewal process completed. Success: {}, Failures: {}", successCount, failureCount);
    }

    private void processRenewal(Subscription subscription) {
        log.info("Processing renewal for subscription: {}", subscription.getSubscriptionId());

        if (subscription.getCardToken() == null) {
            throw new RuntimeException("No payment method available for subscription");
        }

        var plan = subscription.getPlan();
        String idempotencyKey = UUID.randomUUID().toString();

        // Process payment
        PaymentResult result = paymentGateway.processPayment(
                subscription.getCardToken(),
                plan.getPrice(),
                plan.getCurrency(),
                idempotencyKey
        );

        if (result.isSuccess()) {
            // Extend subscription
            LocalDate newEndDate = DateUtil.calculateEndDate(LocalDate.now(), plan.getDurationDays());
            subscription.setEndDate(newEndDate);
            subscription.setNextBillingDate(newEndDate);
            subscription.setAmountPaid(PriceCalculator.add(
                    subscription.getAmountPaid(),
                    plan.getPrice()
            ));

            // Create payment record
            SubscriptionPayment payment = SubscriptionPayment.builder()
                    .subscription(subscription)
                    .amount(plan.getPrice())
                    .currency(plan.getCurrency())
                    .paymentMethod(com.transport.subscription.entity.enums.PaymentMethod.CARD)
                    .paymentType(PaymentType.RENEWAL)
                    .paymentStatus(PaymentStatus.SUCCEEDED)
                    .externalTxnId(result.getExternalTxnId())
                    .idempotencyKey(idempotencyKey)
                    .build();

            subscription.getPayments().add(payment);
            subscription = subscriptionRepository.save(subscription);

            log.info("Subscription renewed successfully: {}", subscription.getSubscriptionId());

            // TODO: Publish renewal event to Kafka if enabled
        } else {
            throw new RuntimeException("Payment failed: " + result.getFailureReason());
        }
    }

    private void handleRenewalFailure(Subscription subscription, String reason) {
        log.warn("Handling renewal failure for subscription: {}", subscription.getSubscriptionId());

        // Create failed payment record
        var plan = subscription.getPlan();
        SubscriptionPayment payment = SubscriptionPayment.builder()
                .subscription(subscription)
                .amount(plan.getPrice())
                .currency(plan.getCurrency())
                .paymentMethod(com.transport.subscription.entity.enums.PaymentMethod.CARD)
                .paymentType(PaymentType.RENEWAL)
                .paymentStatus(PaymentStatus.FAILED)
                .failureReason(reason)
                .idempotencyKey(UUID.randomUUID().toString())
                .build();

        subscription.getPayments().add(payment);

        // Optionally pause or cancel subscription after multiple failures
        // For now, just update next billing date to retry next day
        subscription.setNextBillingDate(LocalDate.now().plusDays(1));
        subscriptionRepository.save(subscription);

        // TODO: Publish failure event to Kafka if enabled
    }
}

