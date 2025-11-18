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
public class SubscriptionCreatedEvent {
    private Integer subscriptionId;
    private Integer userId;
    private Integer planId;
    private OffsetDateTime timestamp;
}

