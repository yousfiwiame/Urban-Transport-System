package com.transport.subscription.service;

import java.util.UUID;

public interface QRCodeService {
    String generateQRCode(UUID subscriptionId);
    boolean validateQRCode(String qrCodeData);
    String getQRCodeData(UUID subscriptionId);
}

