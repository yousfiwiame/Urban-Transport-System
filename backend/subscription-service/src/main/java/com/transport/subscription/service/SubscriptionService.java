package com.transport.subscription.service;

import com.transport.subscription.dto.request.CancelSubscriptionRequest;
import com.transport.subscription.dto.request.CreateSubscriptionRequest;
import com.transport.subscription.dto.request.ProcessPaymentRequest;
import com.transport.subscription.dto.request.RenewSubscriptionRequest;
import com.transport.subscription.dto.request.UpdateSubscriptionRequest;
import com.transport.subscription.dto.response.QRCodeResponse;
import com.transport.subscription.dto.response.SubscriptionResponse;

import java.util.List;

public interface SubscriptionService {
    SubscriptionResponse createSubscription(CreateSubscriptionRequest request);
    SubscriptionResponse getSubscriptionById(Integer subscriptionId);
    List<SubscriptionResponse> getSubscriptionsByUserId(Integer userId);
    List<SubscriptionResponse> getActiveSubscriptionsByUserId(Integer userId);
    SubscriptionResponse cancelSubscription(CancelSubscriptionRequest request);
    SubscriptionResponse renewSubscription(RenewSubscriptionRequest request);
    SubscriptionResponse pauseSubscription(Integer subscriptionId);
    SubscriptionResponse resumeSubscription(Integer subscriptionId);
    QRCodeResponse generateQRCode(Integer subscriptionId);
    boolean validateQRCode(String qrCodeData);
    SubscriptionResponse updateSubscription(Integer subscriptionId, UpdateSubscriptionRequest request);
    SubscriptionResponse retryPayment(Integer subscriptionId, ProcessPaymentRequest paymentRequest);
}

