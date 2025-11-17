package com.transport.subscription.controller;

import com.transport.subscription.dto.request.CancelSubscriptionRequest;
import com.transport.subscription.dto.request.CreateSubscriptionRequest;
import com.transport.subscription.dto.request.RenewSubscriptionRequest;
import com.transport.subscription.dto.request.UpdateSubscriptionRequest;
import com.transport.subscription.dto.response.QRCodeResponse;
import com.transport.subscription.dto.response.SubscriptionResponse;
import com.transport.subscription.service.SubscriptionService;
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
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscription", description = "Subscription management APIs")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @Operation(summary = "Create a new subscription")
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @Valid @RequestBody CreateSubscriptionRequest request) {
        log.info("Creating subscription for user: {}", request.getUserId());
        SubscriptionResponse response = subscriptionService.createSubscription(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subscription by ID")
    public ResponseEntity<SubscriptionResponse> getSubscription(@PathVariable Integer id) {
        SubscriptionResponse response = subscriptionService.getSubscriptionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all subscriptions for a user")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsByUser(
            @PathVariable Integer userId) {
        List<SubscriptionResponse> responses = subscriptionService.getSubscriptionsByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Get active subscriptions for a user")
    public ResponseEntity<List<SubscriptionResponse>> getActiveSubscriptionsByUser(
            @PathVariable Integer userId) {
        List<SubscriptionResponse> responses = subscriptionService.getActiveSubscriptionsByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel a subscription")
    public ResponseEntity<SubscriptionResponse> cancelSubscription(
            @PathVariable Integer id,
            @Valid @RequestBody CancelSubscriptionRequest request) {
        request.setSubscriptionId(id);
        SubscriptionResponse response = subscriptionService.cancelSubscription(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/renew")
    @Operation(summary = "Renew a subscription")
    public ResponseEntity<SubscriptionResponse> renewSubscription(
            @PathVariable Integer id,
            @Valid @RequestBody RenewSubscriptionRequest request) {
        request.setSubscriptionId(id);
        SubscriptionResponse response = subscriptionService.renewSubscription(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/pause")
    @Operation(summary = "Pause a subscription")
    public ResponseEntity<SubscriptionResponse> pauseSubscription(@PathVariable Integer id) {
        SubscriptionResponse response = subscriptionService.pauseSubscription(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/resume")
    @Operation(summary = "Resume a paused subscription")
    public ResponseEntity<SubscriptionResponse> resumeSubscription(@PathVariable Integer id) {
        SubscriptionResponse response = subscriptionService.resumeSubscription(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/qrcode")
    @Operation(summary = "Get QR code for a subscription")
    public ResponseEntity<QRCodeResponse> getQRCode(@PathVariable Integer id) {
        QRCodeResponse response = subscriptionService.generateQRCode(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-qrcode")
    @Operation(summary = "Validate QR code")
    public ResponseEntity<Boolean> validateQRCode(@RequestParam String qrCodeData) {
        boolean isValid = subscriptionService.validateQRCode(qrCodeData);
        return ResponseEntity.ok(isValid);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update subscription")
    public ResponseEntity<SubscriptionResponse> updateSubscription(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateSubscriptionRequest request) {
        SubscriptionResponse response = subscriptionService.updateSubscription(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/retry-payment")
    @Operation(summary = "Retry payment for a pending subscription")
    public ResponseEntity<SubscriptionResponse> retryPayment(
            @PathVariable Integer id,
            @Valid @RequestBody com.transport.subscription.dto.request.ProcessPaymentRequest paymentRequest) {
        log.info("Retrying payment for subscription: {}", id);
        paymentRequest.setSubscriptionId(id);
        SubscriptionResponse response = subscriptionService.retryPayment(id, paymentRequest);
        return ResponseEntity.ok(response);
    }
}

