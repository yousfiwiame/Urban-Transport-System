package com.transport.subscription.scheduler;

import com.transport.subscription.entity.Subscription;
import com.transport.subscription.entity.SubscriptionHistory;
import com.transport.subscription.entity.enums.SubscriptionStatus;
import com.transport.subscription.repository.SubscriptionHistoryRepository;
import com.transport.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Scheduler pour nettoyer les abonnements PENDING abandonnés.
 * Annule automatiquement les abonnements PENDING créés il y a plus de X jours
 * sans paiement réussi.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionCleanupScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryRepository historyRepository;

    @Value("${scheduler.cleanup.enabled:true}")
    private boolean cleanupEnabled;

    @Value("${scheduler.cleanup.pending.days:7}")
    private int pendingDaysThreshold;

    /**
     * Nettoie les abonnements PENDING abandonnés quotidiennement à 2h du matin.
     * Cron: second minute hour day month weekday
     * 0 0 2 * * * = Tous les jours à 2h00
     */
    @Scheduled(cron = "${scheduler.cleanup.cron:0 0 2 * * *}")
    @Transactional
    public void cleanupAbandonedPendingSubscriptions() {
        if (!cleanupEnabled) {
            log.debug("Cleanup scheduler is disabled");
            return;
        }

        log.info("Starting cleanup of abandoned PENDING subscriptions");

        OffsetDateTime thresholdDate = OffsetDateTime.now().minusDays(pendingDaysThreshold);

        // Trouver les abonnements PENDING créés il y a plus de X jours
        List<Subscription> abandonedSubscriptions = subscriptionRepository
                .findAbandonedPendingSubscriptions(SubscriptionStatus.PENDING, thresholdDate);

        log.info("Found {} abandoned PENDING subscriptions to cancel", abandonedSubscriptions.size());

        int cancelledCount = 0;

        for (Subscription subscription : abandonedSubscriptions) {
            try {
                cancelAbandonedSubscription(subscription);
                cancelledCount++;
            } catch (Exception e) {
                log.error("Failed to cancel abandoned subscription {}: {}", 
                        subscription.getSubscriptionId(), e.getMessage(), e);
                // Continue avec les autres abonnements même en cas d'erreur
            }
        }

        log.info("Cleanup process completed. Cancelled: {}", cancelledCount);
    }

    /**
     * Annule un abonnement PENDING abandonné.
     */
    private void cancelAbandonedSubscription(Subscription subscription) {
        log.info("Cancelling abandoned PENDING subscription: {} (created: {})", 
                subscription.getSubscriptionId(), subscription.getCreatedAt());

        SubscriptionStatus oldStatus = subscription.getStatus();
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setAutoRenewEnabled(false);

        subscription = subscriptionRepository.save(subscription);

        // Créer un enregistrement d'historique
        SubscriptionHistory history = SubscriptionHistory.builder()
                .subscription(subscription)
                .oldStatus(oldStatus)
                .newStatus(SubscriptionStatus.CANCELLED)
                .eventType("SUBSCRIPTION_AUTO_CANCELLED")
                .details(String.format("Subscription automatically cancelled after %d days without successful payment. Created: %s", 
                        pendingDaysThreshold, subscription.getCreatedAt()))
                .build();

        historyRepository.save(history);

        log.info("Abandoned subscription {} cancelled successfully (was {})", 
                subscription.getSubscriptionId(), oldStatus);
    }
}

