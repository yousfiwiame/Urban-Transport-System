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
public class SubscriptionCancelledEvent {
    private Integer subscriptionId;
    private Integer userId;
    private String reason;
    private OffsetDateTime timestamp;
}

