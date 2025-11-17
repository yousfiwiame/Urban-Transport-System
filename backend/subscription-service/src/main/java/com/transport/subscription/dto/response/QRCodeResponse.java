package com.transport.subscription.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QRCodeResponse {

    private Integer subscriptionId;
    private String qrCodeData;
    private String qrCodeImageBase64; // Optional: base64 encoded QR code image
}

