package com.transport.subscription.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentProcessedEvent {
    private UUID paymentId;
    private UUID subscriptionId;
    private String status;
    private OffsetDateTime timestamp;
}

