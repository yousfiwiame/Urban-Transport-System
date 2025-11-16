package com.transport.subscription.service;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentResult processPayment(String cardToken, BigDecimal amount, String currency, String idempotencyKey);
    PaymentResult refundPayment(String externalTxnId, BigDecimal amount);
    boolean verifyWebhookSignature(String payload, String signature);
}

