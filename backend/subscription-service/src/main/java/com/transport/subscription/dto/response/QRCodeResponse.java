package com.transport.subscription.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QRCodeResponse {

    private UUID subscriptionId;
    private String qrCodeData;
    private String qrCodeImageBase64; // Optional: base64 encoded QR code image
}

