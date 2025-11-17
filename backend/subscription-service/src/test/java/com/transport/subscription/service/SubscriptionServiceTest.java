package com.transport.subscription.service;

import com.transport.subscription.dto.request.CreateSubscriptionRequest;
import com.transport.subscription.dto.response.SubscriptionResponse;
import com.transport.subscription.entity.SubscriptionPlan;
import com.transport.subscription.entity.enums.PaymentMethod;
import com.transport.subscription.entity.enums.SubscriptionStatus;
import com.transport.subscription.exception.DuplicateSubscriptionException;
import com.transport.subscription.exception.PlanNotFoundException;
import com.transport.subscription.repository.SubscriptionPlanRepository;
import com.transport.subscription.repository.SubscriptionRepository;
import com.transport.subscription.service.impl.SubscriptionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionPlanRepository planRepository;

    @Mock
    private com.transport.subscription.repository.SubscriptionHistoryRepository historyRepository;

    @Mock
    private com.transport.subscription.service.QRCodeService qrCodeService;

    @Mock
    private com.transport.subscription.service.PaymentService paymentService;

    @Mock
    private com.transport.subscription.dto.mapper.SubscriptionMapper subscriptionMapper;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private SubscriptionPlan testPlan;
    private CreateSubscriptionRequest testRequest;

    @BeforeEach
    void setUp() {
        testPlan = SubscriptionPlan.builder()
                .planId(1)
                .planCode("MONTHLY")
                .description("Monthly subscription")
                .durationDays(30)
                .price(new BigDecimal("29.99"))
                .currency("USD")
                .isActive(true)
                .build();

        testRequest = CreateSubscriptionRequest.builder()
                .userId(100)
                .planId(testPlan.getPlanId())
                .cardToken("card_token_123")
                .cardExpMonth(12)
                .cardExpYear(2025)
                .paymentMethod(PaymentMethod.CARD)
                .autoRenewEnabled(true)
                .build();
    }

    @Test
    void testCreateSubscription_Success() {
        // Given
        when(planRepository.findById(testRequest.getPlanId()))
                .thenReturn(Optional.of(testPlan));
        when(subscriptionRepository.existsByUserIdAndPlanIdAndStatusAndDeletedAtIsNull(
                any(), any(), any())).thenReturn(false);
        when(subscriptionMapper.toEntity(any())).thenReturn(
                com.transport.subscription.entity.Subscription.builder().build());
        when(subscriptionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(qrCodeService.generateQRCode(any())).thenReturn("qr_code_data");
        when(paymentService.processPayment(any())).thenReturn(
                com.transport.subscription.dto.response.PaymentResponse.builder()
                        .paymentStatus(com.transport.subscription.entity.enums.PaymentStatus.SUCCEEDED)
                        .build());
        when(subscriptionMapper.toResponse(any())).thenReturn(
                SubscriptionResponse.builder()
                        .subscriptionId(1)
                        .status(SubscriptionStatus.ACTIVE)
                        .build());

        // When
        SubscriptionResponse response = subscriptionService.createSubscription(testRequest);

        // Then
        assertNotNull(response);
        verify(planRepository).findById(testRequest.getPlanId());
        // Subscription is saved twice: once initially, once after payment processing
        verify(subscriptionRepository, atLeastOnce()).save(any());
    }

    @Test
    void testCreateSubscription_PlanNotFound() {
        // Given
        when(planRepository.findById(testRequest.getPlanId()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(PlanNotFoundException.class, () -> {
            subscriptionService.createSubscription(testRequest);
        });
    }

    @Test
    void testCreateSubscription_DuplicateSubscription() {
        // Given
        when(planRepository.findById(testRequest.getPlanId()))
                .thenReturn(Optional.of(testPlan));
        when(subscriptionRepository.existsByUserIdAndPlanIdAndStatusAndDeletedAtIsNull(
                any(), any(), any())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateSubscriptionException.class, () -> {
            subscriptionService.createSubscription(testRequest);
        });
    }
}

