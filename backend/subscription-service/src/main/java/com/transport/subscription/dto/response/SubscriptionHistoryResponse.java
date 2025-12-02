package com.transport.subscription.dto.response;

import com.transport.subscription.entity.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionHistoryResponse {

    private UUID historyId;
    private UUID subscriptionId;
    private SubscriptionStatus oldStatus;
    private SubscriptionStatus newStatus;
    private String eventType;
    private OffsetDateTime eventDate;
    private UUID performedBy;
    private String details;
    private Map<String, Object> metadata;
}

