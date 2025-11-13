package com.transport.subscription.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.transport.subscription.exception.QRCodeGenerationException;
import com.transport.subscription.service.QRCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
public class QRCodeServiceImpl implements QRCodeService {

    private static final int QR_CODE_SIZE = 300;
    private static final String QR_CODE_PREFIX = "SUB_";

    @Override
    public String generateQRCode(UUID subscriptionId) {
        try {
            String qrData = QR_CODE_PREFIX + subscriptionId.toString() + "_" + System.currentTimeMillis();
            
            // Encode to base64 for storage
            String encodedData = Base64.getEncoder().encodeToString(qrData.getBytes(StandardCharsets.UTF_8));
            
            log.info("Generated QR code for subscription: {}", subscriptionId);
            return encodedData;
        } catch (Exception e) {
            log.error("Failed to generate QR code for subscription: {}", subscriptionId, e);
            throw new QRCodeGenerationException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateQRCode(String qrCodeData) {
        try {
            if (qrCodeData == null || qrCodeData.isEmpty()) {
                return false;
            }

            // Decode from base64
            byte[] decodedBytes = Base64.getDecoder().decode(qrCodeData);
            String decodedData = new String(decodedBytes, StandardCharsets.UTF_8);

            // Validate format
            return decodedData.startsWith(QR_CODE_PREFIX) && decodedData.contains("_");
        } catch (Exception e) {
            log.error("Failed to validate QR code: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getQRCodeData(UUID subscriptionId) {
        return generateQRCode(subscriptionId);
    }
}

