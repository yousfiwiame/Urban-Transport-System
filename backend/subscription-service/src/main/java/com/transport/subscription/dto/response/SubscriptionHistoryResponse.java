package com.transport.subscription.dto.response;

import com.transport.subscription.entity.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionHistoryResponse {

    private Integer historyId;
    private Integer subscriptionId;
    private SubscriptionStatus oldStatus;
    private SubscriptionStatus newStatus;
    private String eventType;
    private OffsetDateTime eventDate;
    private Integer performedBy;
    private String details;
    private Map<String, Object> metadata;
}

