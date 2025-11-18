package com.transport.subscription.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResult {
    private boolean success;
    private String externalTxnId;
    private String failureReason;
    private String cardLast4;
    private String cardBrand;

    public PaymentResult(boolean success, String externalTxnId, String failureReason) {
        this.success = success;
        this.externalTxnId = externalTxnId;
        this.failureReason = failureReason;
    }
}

