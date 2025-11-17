package com.transport.subscription.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentProcessedEvent {
    private Integer paymentId;
    private Integer subscriptionId;
    private String status;
    private OffsetDateTime timestamp;
}

