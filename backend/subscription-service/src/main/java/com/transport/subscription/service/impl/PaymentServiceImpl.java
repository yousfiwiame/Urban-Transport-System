package com.transport.subscription.service.impl;

import com.transport.subscription.dto.mapper.PaymentMapper;
import com.transport.subscription.dto.request.ProcessPaymentRequest;
import com.transport.subscription.dto.response.PaymentResponse;
import com.transport.subscription.entity.Subscription;
import com.transport.subscription.entity.SubscriptionPayment;
import com.transport.subscription.entity.enums.PaymentStatus;
import com.transport.subscription.entity.enums.PaymentType;
import com.transport.subscription.exception.IdempotencyKeyException;
import com.transport.subscription.exception.PaymentFailedException;
import com.transport.subscription.exception.SubscriptionNotFoundException;
import com.transport.subscription.repository.SubscriptionPaymentRepository;
import com.transport.subscription.repository.SubscriptionRepository;
import com.transport.subscription.service.PaymentGateway;
import com.transport.subscription.service.PaymentResult;
import com.transport.subscription.service.PaymentService;
import com.transport.subscription.util.PriceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final SubscriptionPaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentGateway paymentGateway;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        log.info("Processing payment for subscription: {}", request.getSubscriptionId());

        // Check idempotency
        if (paymentRepository.existsByIdempotencyKey(request.getIdempotencyKey())) {
            log.warn("Payment with idempotency key already exists: {}", request.getIdempotencyKey());
            SubscriptionPayment existingPayment = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey())
                    .orElseThrow(() -> new IdempotencyKeyException("Duplicate idempotency key"));
            return paymentMapper.toResponse(existingPayment);
        }

        // Get subscription
        Subscription subscription = subscriptionRepository
                .findBySubscriptionIdAndDeletedAtIsNull(request.getSubscriptionId())
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found: " + request.getSubscriptionId()));

        // Process payment through gateway
        PaymentResult result = paymentGateway.processPayment(
                request.getCardToken(),
                request.getAmount(),
                request.getCurrency(),
                request.getIdempotencyKey()
        );

        // Create payment record
        SubscriptionPayment payment = SubscriptionPayment.builder()
                .subscription(subscription)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .paymentType(request.getPaymentType() != null ? request.getPaymentType() : PaymentType.INITIAL)
                .paymentStatus(result.isSuccess() ? PaymentStatus.SUCCEEDED : PaymentStatus.FAILED)
                .externalTxnId(result.getExternalTxnId())
                .idempotencyKey(request.getIdempotencyKey())
                .failureReason(result.getFailureReason())
                .build();

        payment = paymentRepository.save(payment);

        // Update subscription amount_paid if successful
        if (result.isSuccess()) {
            subscription.setAmountPaid(PriceCalculator.add(
                    subscription.getAmountPaid(),
                    request.getAmount()
            ));
            subscriptionRepository.save(subscription);
            log.info("Payment successful: {}", payment.getPaymentId());
        } else {
            log.error("Payment failed: {}", result.getFailureReason());
            throw new PaymentFailedException("Payment failed: " + result.getFailureReason());
        }

        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse refundPayment(Integer paymentId, String reason) {
        log.info("Processing refund for payment: {}", paymentId);

        SubscriptionPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentFailedException("Payment not found: " + paymentId));

        if (payment.getPaymentStatus() != PaymentStatus.SUCCEEDED) {
            throw new PaymentFailedException("Cannot refund payment with status: " + payment.getPaymentStatus());
        }

        if (payment.getExternalTxnId() == null) {
            throw new PaymentFailedException("Payment does not have external transaction ID");
        }

        // Process refund through gateway
        PaymentResult result = paymentGateway.refundPayment(
                payment.getExternalTxnId(),
                payment.getAmount()
        );

        if (result.isSuccess()) {
            payment.setPaymentStatus(PaymentStatus.REFUNDED);
            payment.setFailureReason(reason);
            payment = paymentRepository.save(payment);

            // Update subscription amount_paid
            Subscription subscription = payment.getSubscription();
            subscription.setAmountPaid(PriceCalculator.subtract(
                    subscription.getAmountPaid(),
                    payment.getAmount()
            ));
            subscriptionRepository.save(subscription);

            log.info("Refund successful: {}", paymentId);
        } else {
            throw new PaymentFailedException("Refund failed: " + result.getFailureReason());
        }

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsBySubscriptionId(Integer subscriptionId) {
        List<SubscriptionPayment> payments = paymentRepository
                .findBySubscription_SubscriptionId(subscriptionId);
        return paymentMapper.toResponseList(payments);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Integer paymentId) {
        SubscriptionPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentFailedException("Payment not found: " + paymentId));
        return paymentMapper.toResponse(payment);
    }
}

