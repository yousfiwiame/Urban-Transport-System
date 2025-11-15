package com.transport.subscription.unit;

import com.transport.subscription.service.PaymentGateway;
import com.transport.subscription.service.PaymentResult;
import com.transport.subscription.service.impl.MockPaymentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Mock Payment Gateway Unit Tests")
class MockPaymentGatewayTest {

    private PaymentGateway paymentGateway;

    @BeforeEach
    void setUp() {
        paymentGateway = new MockPaymentGateway();
    }

    @Test
    @DisplayName("Should process payment successfully")
    void testProcessPayment_Success() {
        // Given
        String cardToken = "tok_visa_test";
        BigDecimal amount = new BigDecimal("100.00");
        String currency = "MAD";
        String idempotencyKey = "idempotency_key_123";

        // When
        PaymentResult result = paymentGateway.processPayment(cardToken, amount, currency, idempotencyKey);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getExternalTxnId()).isNotNull();
        assertThat(result.getExternalTxnId()).startsWith("mock_txn_");
        assertThat(result.getFailureReason()).isNull();
    }

    @Test
    @DisplayName("Should process refund successfully")
    void testRefundPayment_Success() {
        // Given
        String externalTxnId = "mock_txn_12345";
        BigDecimal amount = new BigDecimal("50.00");

        // When
        PaymentResult result = paymentGateway.refundPayment(externalTxnId, amount);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getExternalTxnId()).isNotNull();
        assertThat(result.getExternalTxnId()).startsWith("mock_refund_");
    }

    @Test
    @DisplayName("Should verify webhook signature")
    void testVerifyWebhookSignature() {
        // Given
        String payload = "test_payload";
        String signature = "test_signature";

        // When
        boolean isValid = paymentGateway.verifyWebhookSignature(payload, signature);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject null signature")
    void testVerifyWebhookSignature_Null() {
        // Given
        String payload = "test_payload";
        String signature = null;

        // When
        boolean isValid = paymentGateway.verifyWebhookSignature(payload, signature);

        // Then
        assertThat(isValid).isFalse();
    }
}

