package com.transport.subscription.service.impl;

import com.transport.subscription.dto.mapper.SubscriptionMapper;
import com.transport.subscription.dto.request.CancelSubscriptionRequest;
import com.transport.subscription.dto.request.CreateSubscriptionRequest;
import com.transport.subscription.dto.request.ProcessPaymentRequest;
import com.transport.subscription.dto.request.RenewSubscriptionRequest;
import com.transport.subscription.dto.request.UpdateSubscriptionRequest;
import com.transport.subscription.dto.response.PaymentResponse;
import com.transport.subscription.dto.response.QRCodeResponse;
import com.transport.subscription.dto.response.SubscriptionResponse;
import com.transport.subscription.entity.Subscription;
import com.transport.subscription.entity.SubscriptionHistory;
import com.transport.subscription.entity.enums.SubscriptionStatus;
import com.transport.subscription.exception.DuplicateSubscriptionException;
import com.transport.subscription.exception.InvalidSubscriptionException;
import com.transport.subscription.exception.PlanNotFoundException;
import com.transport.subscription.exception.SubscriptionNotFoundException;
import com.transport.subscription.repository.SubscriptionHistoryRepository;
import com.transport.subscription.repository.SubscriptionPlanRepository;
import com.transport.subscription.repository.SubscriptionRepository;
import com.transport.subscription.service.PaymentService;
import com.transport.subscription.service.QRCodeService;
import com.transport.subscription.service.SubscriptionService;
import com.transport.subscription.util.CardValidationUtil;
import com.transport.subscription.util.DateUtil;
import com.transport.subscription.util.PriceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID; // Keep for UUID.randomUUID() for idempotency keys

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionHistoryRepository historyRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final QRCodeService qrCodeService;
    private final PaymentService paymentService;

    @Override
    public SubscriptionResponse createSubscription(CreateSubscriptionRequest request) {
        log.info("Creating subscription for user: {} with plan: {}", request.getUserId(), request.getPlanId());

        // 1. Validate plan exists and is active
        var plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new PlanNotFoundException("Plan not found: " + request.getPlanId()));

        if (!plan.getIsActive()) {
            throw new PlanNotFoundException("Plan is not active: " + request.getPlanId());
        }

        // 2. Validate card expiration date
        if (CardValidationUtil.isCardExpired(request.getCardExpMonth(), request.getCardExpYear())) {
            throw new InvalidSubscriptionException(
                    "Card is expired. Please use a valid payment method.");
        }

        if (!CardValidationUtil.isValidExpirationDate(request.getCardExpMonth(), request.getCardExpYear())) {
            throw new InvalidSubscriptionException("Invalid card expiration date");
        }

        // 3. Check for existing active subscription
        if (subscriptionRepository.existsByUserIdAndPlanIdAndStatusAndDeletedAtIsNull(
                request.getUserId(),
                request.getPlanId(),
                SubscriptionStatus.ACTIVE)) {
            throw new DuplicateSubscriptionException(
                    "User already has an active subscription for this plan");
        }

        // 4. Calculate dates
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = DateUtil.calculateEndDate(startDate, plan.getDurationDays());
        LocalDate nextBillingDate = DateUtil.calculateNextBillingDate(startDate, plan.getDurationDays());

        // 5. Create subscription entity
        Subscription subscription = subscriptionMapper.toEntity(request);
        subscription.setUserId(request.getUserId());
        subscription.setPlan(plan);
        subscription.setStatus(SubscriptionStatus.PENDING);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setNextBillingDate(nextBillingDate);
        subscription.setAutoRenewEnabled(request.getAutoRenewEnabled());
        subscription.setCardToken(request.getCardToken());
        subscription.setCardExpMonth(request.getCardExpMonth());
        subscription.setCardExpYear(request.getCardExpYear());

        subscription = subscriptionRepository.save(subscription);

        // 6. Process initial payment
        // ✅ La subscription est déjà sauvegardée en PENDING
        // Si le paiement échoue, elle reste PENDING pour permettre un retry
        try {
            var paymentRequest = com.transport.subscription.dto.request.ProcessPaymentRequest.builder()
                    .subscriptionId(subscription.getSubscriptionId())
                    .amount(plan.getPrice())
                    .currency(plan.getCurrency())
                    .paymentMethod(request.getPaymentMethod())
                    .cardToken(request.getCardToken())
                    .idempotencyKey(UUID.randomUUID().toString())
                    .build();

            var paymentResponse = paymentService.processPayment(paymentRequest);

            if (paymentResponse.getPaymentStatus() == com.transport.subscription.entity.enums.PaymentStatus.SUCCEEDED) {
                subscription.setStatus(SubscriptionStatus.ACTIVE);
                subscription.setAmountPaid(plan.getPrice());
                log.info("✅ Payment succeeded, subscription activated: {}", subscription.getSubscriptionId());
            } else {
                // ❌ Paiement échoué : reste PENDING pour permettre un retry
                log.warn("⚠️ Payment failed, subscription remains PENDING: {}. Reason: {}", 
                        subscription.getSubscriptionId(), 
                        paymentResponse.getFailureReason());
                // La subscription reste PENDING - l'utilisateur pourra réessayer
            }
        } catch (Exception e) {
            // ❌ Exception lors du paiement : reste PENDING pour permettre un retry
            log.error("❌ Payment processing failed, subscription remains PENDING: {}. Error: {}", 
                    subscription.getSubscriptionId(), e.getMessage());
            // La subscription reste PENDING - l'utilisateur pourra réessayer
        }

        // 6. Generate QR code
        try {
            String qrCodeData = qrCodeService.generateQRCode(subscription.getSubscriptionId());
            subscription.setQrCodeData(qrCodeData);
        } catch (Exception e) {
            log.error("QR code generation failed: {}", e.getMessage());
            // Don't fail subscription creation if QR code fails
        }

        subscription = subscriptionRepository.save(subscription);

        // 7. Create history record
        createHistoryRecord(subscription, null, subscription.getStatus(), "SUBSCRIPTION_CREATED", null);

        log.info("Subscription created successfully: {}", subscription.getSubscriptionId());
        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    @Transactional
    public SubscriptionResponse getSubscriptionById(Integer subscriptionId) {
        Subscription subscription = subscriptionRepository
                .findBySubscriptionIdAndDeletedAtIsNull(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found: " + subscriptionId));
        
        // Valider et corriger le statut si nécessaire (expiration)
        validateAndFixSubscriptionStatus(subscription);
        
        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    @Transactional
    public List<SubscriptionResponse> getSubscriptionsByUserId(Integer userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        
        // Valider et corriger le statut de chaque abonnement si nécessaire
        subscriptions.forEach(this::validateAndFixSubscriptionStatus);
        
        return subscriptionMapper.toResponseList(subscriptions);
    }

    @Override
    @Transactional
    public List<SubscriptionResponse> getActiveSubscriptionsByUserId(Integer userId) {
        List<Subscription> subscriptions = subscriptionRepository.findActiveSubscriptionsByUserId(
                userId, SubscriptionStatus.ACTIVE);
        
        // Filtrer et valider : ne retourner que les abonnements vraiment actifs (non expirés)
        List<Subscription> validActiveSubscriptions = subscriptions.stream()
                .filter(sub -> {
                    validateAndFixSubscriptionStatus(sub);
                    // Ne retourner que ceux qui sont toujours ACTIVE après validation
                    return sub.getStatus() == SubscriptionStatus.ACTIVE;
                })
                .toList();
        
        return subscriptionMapper.toResponseList(validActiveSubscriptions);
    }

    @Override
    public SubscriptionResponse cancelSubscription(CancelSubscriptionRequest request) {
        log.info("Cancelling subscription: {}", request.getSubscriptionId());

        Subscription subscription = subscriptionRepository
                .findBySubscriptionIdAndDeletedAtIsNull(request.getSubscriptionId())
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found: " + request.getSubscriptionId()));

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED ||
            subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            throw new InvalidSubscriptionException("Subscription is already cancelled or expired");
        }

        SubscriptionStatus oldStatus = subscription.getStatus();
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setAutoRenewEnabled(false);
        subscription = subscriptionRepository.save(subscription);

        // Create history record
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("reason", request.getReason());
        metadata.put("refundRequested", request.getRefundRequested());
        createHistoryRecord(subscription, oldStatus, SubscriptionStatus.CANCELLED,
                "SUBSCRIPTION_CANCELLED", metadata);

        log.info("Subscription cancelled: {}", request.getSubscriptionId());
        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    public SubscriptionResponse renewSubscription(RenewSubscriptionRequest request) {
        log.info("Renewing subscription: {}", request.getSubscriptionId());

        Subscription subscription = subscriptionRepository
                .findBySubscriptionIdAndDeletedAtIsNull(request.getSubscriptionId())
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found: " + request.getSubscriptionId()));

        // Valider et corriger le statut si nécessaire (expiration)
        validateAndFixSubscriptionStatus(subscription);

        // Permettre le renouvellement d'abonnements ACTIVE ou EXPIRED récemment (dans les 7 jours)
        boolean canRenew = subscription.getStatus() == SubscriptionStatus.ACTIVE;
        if (!canRenew && subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            LocalDate expirationDate = subscription.getEndDate();
            if (expirationDate != null) {
                long daysSinceExpiration = DateUtil.daysBetween(expirationDate, LocalDate.now());
                canRenew = daysSinceExpiration <= 7; // Permettre renouvellement dans les 7 jours après expiration
            }
        }

        if (!canRenew) {
            throw new InvalidSubscriptionException(
                    "Only active subscriptions or recently expired subscriptions (within 7 days) can be renewed");
        }

        // Update payment method if provided
        if (!request.getUseStoredPaymentMethod() && request.getNewCardToken() != null) {
            // Valider la nouvelle carte
            if (CardValidationUtil.isCardExpired(request.getNewCardExpMonth(), request.getNewCardExpYear())) {
                throw new InvalidSubscriptionException(
                        "New card is expired. Please use a valid payment method.");
            }
            if (!CardValidationUtil.isValidExpirationDate(request.getNewCardExpMonth(), request.getNewCardExpYear())) {
                throw new InvalidSubscriptionException("Invalid new card expiration date");
            }
            subscription.setCardToken(request.getNewCardToken());
            subscription.setCardExpMonth(request.getNewCardExpMonth());
            subscription.setCardExpYear(request.getNewCardExpYear());
        } else {
            // Valider la carte stockée si on l'utilise
            if (CardValidationUtil.isCardExpired(subscription.getCardExpMonth(), subscription.getCardExpYear())) {
                throw new InvalidSubscriptionException(
                        "Stored card is expired. Please provide a new payment method.");
            }
        }

        // Process renewal payment
        var plan = subscription.getPlan();
        try {
            var paymentRequest = com.transport.subscription.dto.request.ProcessPaymentRequest.builder()
                    .subscriptionId(subscription.getSubscriptionId())
                    .amount(plan.getPrice())
                    .currency(plan.getCurrency())
                    .paymentMethod(com.transport.subscription.entity.enums.PaymentMethod.CARD)
                    .cardToken(subscription.getCardToken())
                    .idempotencyKey(UUID.randomUUID().toString())
                    .paymentType(com.transport.subscription.entity.enums.PaymentType.RENEWAL)
                    .build();

            var paymentResponse = paymentService.processPayment(paymentRequest);

            if (paymentResponse.getPaymentStatus() == com.transport.subscription.entity.enums.PaymentStatus.SUCCEEDED) {
                // Extend subscription - preserve days by starting from endDate + 1
                LocalDate newStartDate = subscription.getEndDate() != null 
                    ? subscription.getEndDate().plusDays(1) 
                    : LocalDate.now();
                LocalDate newEndDate = DateUtil.calculateEndDate(newStartDate, plan.getDurationDays());
                subscription.setEndDate(newEndDate);
                subscription.setNextBillingDate(newEndDate);
                subscription.setAmountPaid(PriceCalculator.add(subscription.getAmountPaid(), plan.getPrice()));

                createHistoryRecord(subscription, SubscriptionStatus.ACTIVE, SubscriptionStatus.ACTIVE,
                        "SUBSCRIPTION_RENEWED", null);

                log.info("Subscription renewed successfully: {}", request.getSubscriptionId());
            } else {
                throw new InvalidSubscriptionException("Renewal payment failed");
            }
        } catch (Exception e) {
            log.error("Renewal payment failed: {}", e.getMessage());
            throw new InvalidSubscriptionException("Renewal payment failed: " + e.getMessage());
        }

        subscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    public SubscriptionResponse pauseSubscription(Integer subscriptionId) {
        log.info("Pausing subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository
                .findBySubscriptionIdAndDeletedAtIsNull(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found: " + subscriptionId));

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new InvalidSubscriptionException("Only active subscriptions can be paused");
        }

        SubscriptionStatus oldStatus = subscription.getStatus();
        subscription.setStatus(SubscriptionStatus.PAUSED);
        subscription = subscriptionRepository.save(subscription);

        createHistoryRecord(subscription, oldStatus, SubscriptionStatus.PAUSED,
                "SUBSCRIPTION_PAUSED", null);

        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    public SubscriptionResponse resumeSubscription(Integer subscriptionId) {
        log.info("Resuming subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository
                .findBySubscriptionIdAndDeletedAtIsNull(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found: " + subscriptionId));

        if (subscription.getStatus() != SubscriptionStatus.PAUSED) {
            throw new InvalidSubscriptionException("Only paused subscriptions can be resumed");
        }

        // Valider que l'abonnement n'est pas expiré avant de reprendre
        validateAndFixSubscriptionStatus(subscription);
        
        if (subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            throw new InvalidSubscriptionException(
                    "Cannot resume expired subscription. Please renew it first.");
        }

        SubscriptionStatus oldStatus = subscription.getStatus();
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription = subscriptionRepository.save(subscription);

        createHistoryRecord(subscription, oldStatus, SubscriptionStatus.ACTIVE,
                "SUBSCRIPTION_RESUMED", null);

        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    public QRCodeResponse generateQRCode(Integer subscriptionId) {
        Subscription subscription = subscriptionRepository
                .findBySubscriptionIdAndDeletedAtIsNull(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found: " + subscriptionId));

        String qrCodeData = qrCodeService.generateQRCode(subscriptionId);
        subscription.setQrCodeData(qrCodeData);
        subscriptionRepository.save(subscription);

        return QRCodeResponse.builder()
                .subscriptionId(subscriptionId)
                .qrCodeData(qrCodeData)
                .build();
    }

    @Override
    public boolean validateQRCode(String qrCodeData) {
        return qrCodeService.validateQRCode(qrCodeData);
    }

    @Override
    public SubscriptionResponse updateSubscription(Integer subscriptionId, UpdateSubscriptionRequest request) {
        log.info("Updating subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository
                .findBySubscriptionIdAndDeletedAtIsNull(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found: " + subscriptionId));

        subscriptionMapper.updateEntityFromRequest(request, subscription);
        subscription = subscriptionRepository.save(subscription);

        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    public SubscriptionResponse retryPayment(Integer subscriptionId, ProcessPaymentRequest paymentRequest) {
        log.info("Retrying payment for subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository
                .findBySubscriptionIdAndDeletedAtIsNull(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found: " + subscriptionId));

        // Vérifier que le status est PENDING
        if (subscription.getStatus() != SubscriptionStatus.PENDING) {
            throw new InvalidSubscriptionException(
                    "Can only retry payment for PENDING subscriptions. Current status: " + subscription.getStatus());
        }

        // S'assurer que le subscriptionId dans la requête correspond
        paymentRequest.setSubscriptionId(subscriptionId);

        // Tenter le paiement
        try {
            PaymentResponse paymentResponse = paymentService.processPayment(paymentRequest);

            if (paymentResponse.getPaymentStatus() == com.transport.subscription.entity.enums.PaymentStatus.SUCCEEDED) {
                // Activer l'abonnement
                SubscriptionStatus oldStatus = subscription.getStatus();
                subscription.setStatus(SubscriptionStatus.ACTIVE);
                subscription.setAmountPaid(paymentRequest.getAmount());
                subscription = subscriptionRepository.save(subscription);

                // Créer un historique
                createHistoryRecord(subscription, oldStatus, SubscriptionStatus.ACTIVE,
                        "PAYMENT_RETRY_SUCCEEDED", null);

                log.info("✅ Subscription activated after payment retry: {}", subscriptionId);
            } else {
                log.warn("⚠️ Payment retry failed, subscription remains PENDING: {}. Reason: {}",
                        subscriptionId, paymentResponse.getFailureReason());
                throw new InvalidSubscriptionException(
                        "Payment retry failed: " + paymentResponse.getFailureReason());
            }

        } catch (InvalidSubscriptionException e) {
            throw e; // Re-lancer les exceptions métier
        } catch (Exception e) {
            log.error("❌ Payment retry failed with exception: {}", e.getMessage(), e);
            throw new InvalidSubscriptionException("Payment retry failed: " + e.getMessage());
        }

        return subscriptionMapper.toResponse(subscription);
    }

    /**
     * Valide et corrige le statut d'un abonnement si nécessaire.
     * Si l'abonnement est ACTIVE ou PAUSED mais que sa date de fin est passée,
     * il est automatiquement marqué comme EXPIRED.
     * 
     * Cette méthode garantit la cohérence des données et évite de retourner
     * des abonnements expirés comme ACTIVE.
     */
    private void validateAndFixSubscriptionStatus(Subscription subscription) {
        if (subscription == null || subscription.getDeletedAt() != null) {
            return;
        }

        LocalDate today = LocalDate.now();
        SubscriptionStatus currentStatus = subscription.getStatus();

        // Vérifier si l'abonnement devrait être expiré
        if ((currentStatus == SubscriptionStatus.ACTIVE || currentStatus == SubscriptionStatus.PAUSED)
                && subscription.getEndDate() != null
                && subscription.getEndDate().isBefore(today)) {
            
            log.warn("Subscription {} has expired (endDate: {}, current status: {}). Auto-updating to EXPIRED.",
                    subscription.getSubscriptionId(), subscription.getEndDate(), currentStatus);

            SubscriptionStatus oldStatus = subscription.getStatus();
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscription.setAutoRenewEnabled(false);

            subscription = subscriptionRepository.save(subscription);

            // Créer un enregistrement d'historique
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("reason", "End date passed");
            metadata.put("endDate", subscription.getEndDate().toString());
            createHistoryRecord(subscription, oldStatus, SubscriptionStatus.EXPIRED,
                    "SUBSCRIPTION_AUTO_EXPIRED", metadata);

            log.info("Subscription {} status updated from {} to EXPIRED", 
                    subscription.getSubscriptionId(), oldStatus);
        }
    }

    private void createHistoryRecord(Subscription subscription, SubscriptionStatus oldStatus,
                                     SubscriptionStatus newStatus, String eventType, Map<String, Object> metadata) {
        SubscriptionHistory history = SubscriptionHistory.builder()
                .subscription(subscription)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .eventType(eventType)
                .details("Status changed from " + oldStatus + " to " + newStatus)
                .metadata(metadata)
                .build();
        historyRepository.save(history);
    }
}

