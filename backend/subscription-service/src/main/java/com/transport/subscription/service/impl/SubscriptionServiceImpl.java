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
import java.util.UUID;

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

        // 2. Check for existing active subscription
        if (subscriptionRepository.existsByUserIdAndPlanIdAndStatusAndDeletedAtIsNull(
                request.getUserId(),
                request.getPlanId(),
                SubscriptionStatus.ACTIVE)) {
            throw new DuplicateSubscriptionException(
                    "User already has an active subscription for this plan");
        }

        // 3. Calculate dates
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = DateUtil.calculateEndDate(startDate, plan.getDurationDays());
        LocalDate nextBillingDate = DateUtil.calculateNextBillingDate(startDate, plan.getDurationDays());

        // 4. Create subscription entity
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

        // 5. Process initial payment
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
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionById(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository
                .findBySubscriptionIdAndDeletedAtIsNull(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found: " + subscriptionId));
        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getSubscriptionsByUserId(UUID userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        return subscriptionMapper.toResponseList(subscriptions);
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

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new InvalidSubscriptionException("Only active subscriptions can be renewed");
        }

        // Update payment method if provided
        if (!request.getUseStoredPaymentMethod() && request.getNewCardToken() != null) {
            subscription.setCardToken(request.getNewCardToken());
            subscription.setCardExpMonth(request.getNewCardExpMonth());
            subscription.setCardExpYear(request.getNewCardExpYear());
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
    public SubscriptionResponse pauseSubscription(UUID subscriptionId) {
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
    public SubscriptionResponse resumeSubscription(UUID subscriptionId) {
        log.info("Resuming subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository
                .findBySubscriptionIdAndDeletedAtIsNull(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found: " + subscriptionId));

        if (subscription.getStatus() != SubscriptionStatus.PAUSED) {
            throw new InvalidSubscriptionException("Only paused subscriptions can be resumed");
        }

        SubscriptionStatus oldStatus = subscription.getStatus();
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription = subscriptionRepository.save(subscription);

        createHistoryRecord(subscription, oldStatus, SubscriptionStatus.ACTIVE,
                "SUBSCRIPTION_RESUMED", null);

        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    public QRCodeResponse generateQRCode(UUID subscriptionId) {
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
    public SubscriptionResponse updateSubscription(UUID subscriptionId, UpdateSubscriptionRequest request) {
        log.info("Updating subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository
                .findBySubscriptionIdAndDeletedAtIsNull(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found: " + subscriptionId));

        subscriptionMapper.updateEntityFromRequest(request, subscription);
        subscription = subscriptionRepository.save(subscription);

        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    public SubscriptionResponse retryPayment(UUID subscriptionId, ProcessPaymentRequest paymentRequest) {
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

