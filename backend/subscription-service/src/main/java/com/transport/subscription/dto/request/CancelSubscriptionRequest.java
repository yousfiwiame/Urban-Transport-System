package com.transport.subscription.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelSubscriptionRequest {

    @NotNull(message = "Subscription ID is required")
    private Integer subscriptionId;

    private String reason;

    @Builder.Default
    private Boolean refundRequested = false;
}

