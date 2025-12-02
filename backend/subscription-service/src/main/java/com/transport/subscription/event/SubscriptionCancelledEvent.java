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
public class SubscriptionCancelledEvent {
    private UUID subscriptionId;
    private UUID userId;
    private String reason;
    private OffsetDateTime timestamp;
}

