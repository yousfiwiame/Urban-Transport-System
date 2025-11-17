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
public class RenewSubscriptionRequest {

    @NotNull(message = "Subscription ID is required")
    private Integer subscriptionId;

    @Builder.Default
    private Boolean useStoredPaymentMethod = true;

    private String newCardToken;

    private Integer newCardExpMonth;

    private Integer newCardExpYear;
}

