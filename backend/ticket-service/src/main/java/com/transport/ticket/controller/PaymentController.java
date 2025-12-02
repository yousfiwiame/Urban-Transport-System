package com.transport.ticket.controller;

import com.stripe.exception.StripeException;
import com.transport.ticket.service.StripePaymentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controller for handling payment operations via Stripe
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {

    private final StripePaymentService stripePaymentService;

    /**
     * Create a payment intent for ticket purchase
     * POST /api/payments/create-intent
     */
    @PostMapping("/create-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody CreatePaymentIntentRequest request) {
        log.info("📝 [POST /api/payments/create-intent] Creating payment intent - Amount: {} MAD", request.getAmount());

        try {
            String clientSecret = stripePaymentService.createPaymentIntent(
                    request.getAmount(),
                    request.getTicketId(),
                    request.getUserId()
            );

            log.info("✅ [POST /api/payments/create-intent] Payment intent created successfully");

            return ResponseEntity.ok(new CreatePaymentIntentResponse(clientSecret));

        } catch (StripeException e) {
            log.error("❌ [POST /api/payments/create-intent] Stripe error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Failed to create payment intent: " + e.getMessage()));
        } catch (Exception e) {
            log.error("❌ [POST /api/payments/create-intent] Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    /**
     * Verify payment status
     * GET /api/payments/verify/{paymentIntentId}
     */
    @GetMapping("/verify/{paymentIntentId}")
    public ResponseEntity<?> verifyPayment(@PathVariable String paymentIntentId) {
        log.info("🔍 [GET /api/payments/verify/{}] Verifying payment", paymentIntentId);

        try {
            boolean succeeded = stripePaymentService.verifyPayment(paymentIntentId);

            log.info("✅ [GET /api/payments/verify/{}] Payment verified - Status: {}",
                    paymentIntentId, succeeded ? "SUCCEEDED" : "FAILED");

            return ResponseEntity.ok(new VerifyPaymentResponse(succeeded,
                    succeeded ? "Payment succeeded" : "Payment not completed"));

        } catch (StripeException e) {
            log.error("❌ [GET /api/payments/verify/{}] Stripe error: {}", paymentIntentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Failed to verify payment: " + e.getMessage()));
        } catch (Exception e) {
            log.error("❌ [GET /api/payments/verify/{}] Error: {}", paymentIntentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    /**
     * Cancel a payment
     * POST /api/payments/cancel/{paymentIntentId}
     */
    @PostMapping("/cancel/{paymentIntentId}")
    public ResponseEntity<?> cancelPayment(@PathVariable String paymentIntentId) {
        log.info("🚫 [POST /api/payments/cancel/{}] Cancelling payment", paymentIntentId);

        try {
            boolean cancelled = stripePaymentService.cancelPayment(paymentIntentId);

            log.info("✅ [POST /api/payments/cancel/{}] Payment cancelled", paymentIntentId);

            return ResponseEntity.ok(new CancelPaymentResponse(cancelled, "Payment cancelled successfully"));

        } catch (StripeException e) {
            log.error("❌ [POST /api/payments/cancel/{}] Stripe error: {}", paymentIntentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Failed to cancel payment: " + e.getMessage()));
        } catch (Exception e) {
            log.error("❌ [POST /api/payments/cancel/{}] Error: {}", paymentIntentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    /**
     * Refund a payment
     * POST /api/payments/refund
     */
    @PostMapping("/refund")
    public ResponseEntity<?> refundPayment(@RequestBody RefundPaymentRequest request) {
        log.info("💰 [POST /api/payments/refund] Refunding payment: {}", request.getPaymentIntentId());

        try {
            String refundId = stripePaymentService.refundPayment(
                    request.getPaymentIntentId(),
                    request.getAmount()
            );

            log.info("✅ [POST /api/payments/refund] Refund created - ID: {}", refundId);

            return ResponseEntity.ok(new RefundPaymentResponse(refundId, "Refund processed successfully"));

        } catch (StripeException e) {
            log.error("❌ [POST /api/payments/refund] Stripe error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Failed to process refund: " + e.getMessage()));
        } catch (Exception e) {
            log.error("❌ [POST /api/payments/refund] Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error"));
        }
    }

    // DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePaymentIntentRequest {
        private BigDecimal amount;
        private Long ticketId;
        private Long userId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePaymentIntentResponse {
        private String clientSecret;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyPaymentResponse {
        private boolean succeeded;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelPaymentResponse {
        private boolean cancelled;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundPaymentRequest {
        private String paymentIntentId;
        private BigDecimal amount; // null for full refund
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundPaymentResponse {
        private String refundId;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private String error;
    }
}
