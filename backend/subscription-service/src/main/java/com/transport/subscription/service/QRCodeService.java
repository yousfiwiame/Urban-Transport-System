package com.transport.subscription.service;

public interface QRCodeService {
    String generateQRCode(Integer subscriptionId);
    boolean validateQRCode(String qrCodeData);
    String getQRCodeData(Integer subscriptionId);
}

