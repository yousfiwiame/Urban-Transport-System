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

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduler pour mettre à jour automatiquement le statut des abonnements expirés.
 * Vérifie quotidiennement les abonnements dont la date de fin (endDate) est passée
 * et les marque comme EXPIRED.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpirationScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryRepository historyRepository;

    @Value("${scheduler.expiration.enabled:true}")
    private boolean expirationEnabled;

    /**
     * Vérifie et expire les abonnements quotidiennement à 1h du matin.
     * Cron: second minute hour day month weekday
     * 0 0 1 * * * = Tous les jours à 1h00
     */
    @Scheduled(cron = "${scheduler.expiration.cron:0 0 1 * * *}")
    @Transactional
    public void expireSubscriptions() {
        if (!expirationEnabled) {
            log.debug("Expiration scheduler is disabled");
            return;
        }

        log.info("Starting subscription expiration process");

        LocalDate today = LocalDate.now();

        // Trouver tous les abonnements ACTIVE ou PAUSED dont la date de fin est passée
        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findSubscriptionsToExpire(today);

        log.info("Found {} subscriptions to expire", expiredSubscriptions.size());

        int expiredCount = 0;

        for (Subscription subscription : expiredSubscriptions) {
            try {
                expireSubscription(subscription);
                expiredCount++;
            } catch (Exception e) {
                log.error("Failed to expire subscription {}: {}", 
                        subscription.getSubscriptionId(), e.getMessage(), e);
                // Continue avec les autres abonnements même en cas d'erreur
            }
        }

        log.info("Expiration process completed. Expired: {}", expiredCount);
    }

    /**
     * Expire un abonnement en changeant son statut à EXPIRED.
     */
    private void expireSubscription(Subscription subscription) {
        log.info("Expiring subscription: {} (endDate: {})", 
                subscription.getSubscriptionId(), subscription.getEndDate());

        SubscriptionStatus oldStatus = subscription.getStatus();
        subscription.setStatus(SubscriptionStatus.EXPIRED);
        subscription.setAutoRenewEnabled(false); // Désactiver le renouvellement automatique

        subscription = subscriptionRepository.save(subscription);

        // Créer un enregistrement d'historique
        SubscriptionHistory history = SubscriptionHistory.builder()
                .subscription(subscription)
                .oldStatus(oldStatus)
                .newStatus(SubscriptionStatus.EXPIRED)
                .eventType("SUBSCRIPTION_EXPIRED")
                .details(String.format("Subscription expired automatically. End date: %s", 
                        subscription.getEndDate()))
                .build();

        historyRepository.save(history);

        log.info("Subscription {} expired successfully (was {})", 
                subscription.getSubscriptionId(), oldStatus);
    }
}

