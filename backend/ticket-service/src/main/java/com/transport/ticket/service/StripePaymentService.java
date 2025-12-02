package com.transport.ticket.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for handling Stripe payments
 */
@Service
@Slf4j
public class StripePaymentService {

    @Value("${stripe.api.key:sk_test_your_key_here}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
        log.info("Stripe API initialized");
    }

    /**
     * Create a payment intent for ticket purchase
     *
     * @param amount Amount in MAD
     * @param ticketId Ticket ID for reference
     * @param userId User ID for reference
     * @return Client secret for frontend to complete payment
     * @throws StripeException if payment intent creation fails
     */
    public String createPaymentIntent(BigDecimal amount, Long ticketId, Long userId) throws StripeException {
        log.info("Creating payment intent for ticket {} - Amount: {} MAD", ticketId, amount);

        // Convert amount to cents (Stripe uses smallest currency unit)
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        // Create metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("ticket_id", ticketId.toString());
        metadata.put("user_id", userId.toString());
        metadata.put("service", "ticket-purchase");

        // Create payment intent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("mad") // Moroccan Dirham
                .putAllMetadata(metadata)
                .setDescription("Ticket purchase for route")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        log.info("Payment intent created successfully - ID: {}", paymentIntent.getId());

        return paymentIntent.getClientSecret();
    }

    /**
     * Verify payment intent status
     *
     * @param paymentIntentId Payment intent ID
     * @return True if payment succeeded, false otherwise
     * @throws StripeException if retrieval fails
     */
    public boolean verifyPayment(String paymentIntentId) throws StripeException {
        log.info("Verifying payment intent: {}", paymentIntentId);

        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        String status = paymentIntent.getStatus();

        log.info("Payment intent {} status: {}", paymentIntentId, status);
        return "succeeded".equals(status);
    }

    /**
     * Cancel a payment intent
     *
     * @param paymentIntentId Payment intent ID
     * @return True if cancellation succeeded
     * @throws StripeException if cancellation fails
     */
    public boolean cancelPayment(String paymentIntentId) throws StripeException {
        log.info("Cancelling payment intent: {}", paymentIntentId);

        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        PaymentIntent cancelledIntent = paymentIntent.cancel();

        log.info("Payment intent {} cancelled - status: {}", paymentIntentId, cancelledIntent.getStatus());
        return "canceled".equals(cancelledIntent.getStatus());
    }

    /**
     * Refund a payment
     *
     * @param paymentIntentId Payment intent ID
     * @param amount Amount to refund (optional, null for full refund)
     * @return Refund ID if successful
     * @throws StripeException if refund fails
     */
    public String refundPayment(String paymentIntentId, BigDecimal amount) throws StripeException {
        log.info("Refunding payment intent: {} - Amount: {}", paymentIntentId, amount);

        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        String chargeId = paymentIntent.getLatestCharge();

        if (chargeId == null) {
            throw new IllegalStateException("No charge found for payment intent: " + paymentIntentId);
        }

        com.stripe.param.RefundCreateParams.Builder paramsBuilder = com.stripe.param.RefundCreateParams.builder()
                .setCharge(chargeId);

        if (amount != null) {
            long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();
            paramsBuilder.setAmount(amountInCents);
        }

        com.stripe.model.Refund refund = com.stripe.model.Refund.create(paramsBuilder.build());
        log.info("Refund created successfully - ID: {}", refund.getId());

        return refund.getId();
    }
}
