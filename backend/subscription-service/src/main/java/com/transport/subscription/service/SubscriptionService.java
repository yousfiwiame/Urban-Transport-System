package com.transport.subscription.service;

import com.transport.subscription.dto.request.CancelSubscriptionRequest;
import com.transport.subscription.dto.request.CreateSubscriptionRequest;
import com.transport.subscription.dto.request.RenewSubscriptionRequest;
import com.transport.subscription.dto.request.UpdateSubscriptionRequest;
import com.transport.subscription.dto.response.QRCodeResponse;
import com.transport.subscription.dto.response.SubscriptionResponse;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {
    SubscriptionResponse createSubscription(CreateSubscriptionRequest request);
    SubscriptionResponse getSubscriptionById(UUID subscriptionId);
    List<SubscriptionResponse> getSubscriptionsByUserId(UUID userId);
    SubscriptionResponse cancelSubscription(CancelSubscriptionRequest request);
    SubscriptionResponse renewSubscription(RenewSubscriptionRequest request);
    SubscriptionResponse pauseSubscription(UUID subscriptionId);
    SubscriptionResponse resumeSubscription(UUID subscriptionId);
    QRCodeResponse generateQRCode(UUID subscriptionId);
    boolean validateQRCode(String qrCodeData);
    SubscriptionResponse updateSubscription(UUID subscriptionId, UpdateSubscriptionRequest request);
}

