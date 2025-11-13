package com.transport.subscription.service;

import com.transport.subscription.dto.request.ProcessPaymentRequest;
import com.transport.subscription.dto.response.PaymentResponse;
import com.transport.subscription.entity.enums.PaymentType;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentResponse processPayment(ProcessPaymentRequest request);
    PaymentResponse refundPayment(UUID paymentId, String reason);
    List<PaymentResponse> getPaymentsBySubscriptionId(UUID subscriptionId);
    PaymentResponse getPaymentById(UUID paymentId);
}

