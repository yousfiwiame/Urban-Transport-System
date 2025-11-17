package com.transport.subscription.service;

import com.transport.subscription.dto.request.ProcessPaymentRequest;
import com.transport.subscription.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(ProcessPaymentRequest request);
    PaymentResponse refundPayment(Integer paymentId, String reason);
    List<PaymentResponse> getPaymentsBySubscriptionId(Integer subscriptionId);
    PaymentResponse getPaymentById(Integer paymentId);
}

