package com.transport.subscription.controller;

import com.transport.subscription.dto.request.ProcessPaymentRequest;
import com.transport.subscription.dto.response.PaymentResponse;
import com.transport.subscription.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment management APIs")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Process a payment")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody ProcessPaymentRequest request) {
        log.info("Processing payment for subscription: {}", request.getSubscriptionId());
        PaymentResponse response = paymentService.processPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/subscription/{subscriptionId}")
    @Operation(summary = "Get all payments for a subscription")
    public ResponseEntity<List<PaymentResponse>> getPaymentsBySubscription(
            @PathVariable Integer subscriptionId) {
        List<PaymentResponse> responses = paymentService.getPaymentsBySubscriptionId(subscriptionId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Integer id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Refund a payment")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable Integer id,
            @RequestParam(required = false) String reason) {
        log.info("Processing refund for payment: {}", id);
        PaymentResponse response = paymentService.refundPayment(id, reason);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    @Operation(summary = "Handle payment webhook")
    public ResponseEntity<String> handlePaymentWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Webhook-Signature", required = false) String signature) {
        log.info("Received payment webhook");
        // TODO: Implement webhook handling
        return ResponseEntity.ok("Webhook received");
    }
}

