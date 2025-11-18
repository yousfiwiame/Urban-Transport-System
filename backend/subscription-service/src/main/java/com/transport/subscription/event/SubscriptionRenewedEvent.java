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
public class SubscriptionRenewedEvent {
    private Integer subscriptionId;
    private Integer userId;
    private OffsetDateTime timestamp;
}

