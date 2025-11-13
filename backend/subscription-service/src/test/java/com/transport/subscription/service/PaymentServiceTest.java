package com.transport.subscription.service;

import com.transport.subscription.dto.request.ProcessPaymentRequest;
import com.transport.subscription.dto.response.PaymentResponse;
import com.transport.subscription.entity.Subscription;
import com.transport.subscription.entity.enums.PaymentMethod;
import com.transport.subscription.entity.enums.PaymentStatus;
import com.transport.subscription.exception.IdempotencyKeyException;
import com.transport.subscription.exception.SubscriptionNotFoundException;
import com.transport.subscription.repository.SubscriptionPaymentRepository;
import com.transport.subscription.repository.SubscriptionRepository;
import com.transport.subscription.service.PaymentResult;
import com.transport.subscription.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private SubscriptionPaymentRepository paymentRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private com.transport.subscription.service.PaymentGateway paymentGateway;

    @Mock
    private com.transport.subscription.dto.mapper.PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private ProcessPaymentRequest testRequest;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {
        testSubscription = Subscription.builder()
                .subscriptionId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .build();

        testRequest = ProcessPaymentRequest.builder()
                .subscriptionId(testSubscription.getSubscriptionId())
                .amount(new BigDecimal("29.99"))
                .currency("USD")
                .paymentMethod(PaymentMethod.CARD)
                .cardToken("card_token_123")
                .idempotencyKey(UUID.randomUUID().toString())
                .build();
    }

    @Test
    void testProcessPayment_Success() {
        // Given
        when(paymentRepository.existsByIdempotencyKey(testRequest.getIdempotencyKey()))
                .thenReturn(false);
        when(subscriptionRepository.findBySubscriptionIdAndDeletedAtIsNull(testRequest.getSubscriptionId()))
                .thenReturn(Optional.of(testSubscription));
        when(paymentGateway.processPayment(any(), any(), any(), any()))
                .thenReturn(new PaymentResult(
                        true, "txn_123", null));
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentMapper.toResponse(any())).thenReturn(
                PaymentResponse.builder()
                        .paymentId(UUID.randomUUID())
                        .paymentStatus(PaymentStatus.SUCCEEDED)
                        .build());

        // When
        PaymentResponse response = paymentService.processPayment(testRequest);

        // Then
        assertNotNull(response);
        verify(paymentGateway).processPayment(any(), any(), any(), any());
        verify(paymentRepository).save(any());
    }

    @Test
    void testProcessPayment_DuplicateIdempotencyKey() {
        // Given
        when(paymentRepository.existsByIdempotencyKey(testRequest.getIdempotencyKey()))
                .thenReturn(true);
        when(paymentRepository.findByIdempotencyKey(testRequest.getIdempotencyKey()))
                .thenReturn(Optional.of(com.transport.subscription.entity.SubscriptionPayment.builder()
                        .paymentId(UUID.randomUUID())
                        .build()));
        when(paymentMapper.toResponse(any())).thenReturn(
                PaymentResponse.builder().build());

        // When
        PaymentResponse response = paymentService.processPayment(testRequest);

        // Then
        assertNotNull(response);
        verify(paymentGateway, never()).processPayment(any(), any(), any(), any());
    }

    @Test
    void testProcessPayment_SubscriptionNotFound() {
        // Given
        when(paymentRepository.existsByIdempotencyKey(testRequest.getIdempotencyKey()))
                .thenReturn(false);
        when(subscriptionRepository.findBySubscriptionIdAndDeletedAtIsNull(testRequest.getSubscriptionId()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(SubscriptionNotFoundException.class, () -> {
            paymentService.processPayment(testRequest);
        });
    }
}

