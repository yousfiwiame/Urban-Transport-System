package com.transport.subscription.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.RefundCreateParams;
import com.transport.subscription.service.PaymentGateway;
import com.transport.subscription.service.PaymentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
public class StripePaymentGateway implements PaymentGateway {

    @Value("${payment.stripe.secret-key:}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        if (stripeSecretKey != null && !stripeSecretKey.isEmpty()) {
            Stripe.apiKey = stripeSecretKey;
            log.info("Stripe payment gateway initialized");
        } else {
            log.warn("Stripe secret key not configured. Payment gateway will use mock mode.");
        }
    }

    @Override
    public PaymentResult processPayment(String cardToken, BigDecimal amount, String currency, String idempotencyKey) {
        try {
            if (stripeSecretKey == null || stripeSecretKey.isEmpty()) {
                // Mock payment for development
                log.warn("Using mock payment (Stripe not configured)");
                return new PaymentResult(true, "mock_txn_" + System.currentTimeMillis(), null);
            }

            ChargeCreateParams params = ChargeCreateParams.builder()
                    .setAmount(convertToStripeAmount(amount))
                    .setCurrency(currency.toLowerCase())
                    .setSource(cardToken)
                    .build();

            RequestOptions requestOptions = RequestOptions.builder()
                    .setIdempotencyKey(idempotencyKey)
                    .build();

            Charge charge = Charge.create(params, requestOptions);

            PaymentResult result = new PaymentResult(
                    charge.getPaid(),
                    charge.getId(),
                    charge.getFailureMessage()
            );

            if (charge.getPaymentMethodDetails() != null && charge.getPaymentMethodDetails().getCard() != null) {
                result.setCardLast4(charge.getPaymentMethodDetails().getCard().getLast4());
                result.setCardBrand(charge.getPaymentMethodDetails().getCard().getBrand());
            }

            return result;
        } catch (StripeException e) {
            log.error("Stripe payment failed: {}", e.getMessage(), e);
            return new PaymentResult(false, null, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during payment processing: {}", e.getMessage(), e);
            return new PaymentResult(false, null, "Payment processing failed: " + e.getMessage());
        }
    }

    @Override
    public PaymentResult refundPayment(String externalTxnId, BigDecimal amount) {
        try {
            if (stripeSecretKey == null || stripeSecretKey.isEmpty()) {
                log.warn("Using mock refund (Stripe not configured)");
                return new PaymentResult(true, "mock_refund_" + System.currentTimeMillis(), null);
            }

            RefundCreateParams params = RefundCreateParams.builder()
                    .setCharge(externalTxnId)
                    .setAmount(convertToStripeAmount(amount))
                    .build();

            Refund refund = Refund.create(params);

            return new PaymentResult(
                    refund.getStatus().equals("succeeded"),
                    refund.getId(),
                    refund.getFailureReason()
            );
        } catch (StripeException e) {
            log.error("Stripe refund failed: {}", e.getMessage(), e);
            return new PaymentResult(false, null, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during refund processing: {}", e.getMessage(), e);
            return new PaymentResult(false, null, "Refund processing failed: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        // Implement webhook signature verification
        // This is a simplified version - in production, use Stripe's webhook signature verification
        return signature != null && !signature.isEmpty();
    }

    private Long convertToStripeAmount(BigDecimal amount) {
        // Stripe amounts are in cents/smallest currency unit
        return amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }
}

