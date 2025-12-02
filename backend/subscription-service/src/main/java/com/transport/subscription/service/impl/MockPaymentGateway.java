package com.transport.subscription.service.impl;

import com.transport.subscription.service.PaymentGateway;
import com.transport.subscription.service.PaymentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mock Payment Gateway for development and testing
 * Simulates successful payments without calling external payment providers
 */
@Slf4j
@Service
@Profile("dev")
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult processPayment(String cardToken, BigDecimal amount, String currency, String idempotencyKey) {
        log.info("MOCK PAYMENT SUCCESS: {} {} (Card: {}, Idempotency: {})", 
                 amount, currency, maskCardToken(cardToken), idempotencyKey);
        
        // Simule un paiement réussi
        String mockTransactionId = "mock_txn_" + UUID.randomUUID().toString().substring(0, 8);
        
        PaymentResult result = new PaymentResult(true, mockTransactionId, null);
        result.setCardLast4("4242");
        result.setCardBrand("Visa");
        
        log.info("Mock payment transaction ID: {}", mockTransactionId);
        return result;
    }

    @Override
    public PaymentResult refundPayment(String externalTxnId, BigDecimal amount) {
        log.info("MOCK REFUND SUCCESS: {} for transaction {}", amount, externalTxnId);
        
        // Simule un remboursement réussi
        String mockRefundId = "mock_refund_" + UUID.randomUUID().toString().substring(0, 8);
        
        log.info("Mock refund ID: {}", mockRefundId);
        return new PaymentResult(true, mockRefundId, null);
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        // En mode mock, on accepte toutes les signatures
        log.debug("Mock webhook signature verification: {}", signature != null ? "ACCEPTED" : "REJECTED");
        return signature != null && !signature.isEmpty();
    }

    /**
     * Masque le token de carte pour les logs (sécurité)
     */
    private String maskCardToken(String cardToken) {
        if (cardToken == null || cardToken.length() <= 4) {
            return "****";
        }
        return "****" + cardToken.substring(cardToken.length() - 4);
    }
}

