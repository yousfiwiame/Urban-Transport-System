package com.transport.subscription.unit;

import com.transport.subscription.service.QRCodeService;
import com.transport.subscription.service.impl.QRCodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("QR Code Service Unit Tests")
class QRCodeServiceTest {

    private QRCodeService qrCodeService;

    @BeforeEach
    void setUp() {
        qrCodeService = new QRCodeServiceImpl();
    }

    @Test
    @DisplayName("Should generate QR code successfully")
    void testGenerateQRCode_Success() {
        // Given
        Integer subscriptionId = 123;

        // When
        String qrCode = qrCodeService.generateQRCode(subscriptionId);

        // Then
        assertThat(qrCode).isNotNull();
        assertThat(qrCode).isNotEmpty();
        // QR code is base64 encoded, so we need to decode it to check the content
        String decoded = new String(java.util.Base64.getDecoder().decode(qrCode));
        assertThat(decoded).contains(subscriptionId.toString());
    }

    @Test
    @DisplayName("Should validate correct QR code")
    void testValidateQRCode_Valid() {
        // Given
        Integer subscriptionId = 123;
        String qrCode = qrCodeService.generateQRCode(subscriptionId);

        // When
        boolean isValid = qrCodeService.validateQRCode(qrCode);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject invalid QR code")
    void testValidateQRCode_Invalid() {
        // Given
        String invalidQRCode = "invalid_qr_code_data";

        // When
        boolean isValid = qrCodeService.validateQRCode(invalidQRCode);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should reject null QR code")
    void testValidateQRCode_Null() {
        // When
        boolean isValid = qrCodeService.validateQRCode(null);

        // Then
        assertThat(isValid).isFalse();
    }
}

