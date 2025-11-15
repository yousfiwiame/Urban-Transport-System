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
public class SubscriptionCreatedEvent {
    private UUID subscriptionId;
    private UUID userId;
    private UUID planId;
    private OffsetDateTime timestamp;
}

