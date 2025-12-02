package com.transport.subscription.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RenewSubscriptionRequest {

    @NotNull(message = "Subscription ID is required")
    private UUID subscriptionId;

    @Builder.Default
    private Boolean useStoredPaymentMethod = true;

    private String newCardToken;

    private Integer newCardExpMonth;

    private Integer newCardExpYear;
}

