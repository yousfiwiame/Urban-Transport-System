package com.transport.subscription.dto.request;

import com.transport.subscription.entity.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubscriptionRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Plan ID is required")
    private UUID planId;

    @NotBlank(message = "Card token is required")
    @Size(max = 128, message = "Card token must not exceed 128 characters")
    private String cardToken;

    @NotNull(message = "Card expiration month is required")
    @Min(value = 1, message = "Card expiration month must be between 1 and 12")
    @Max(value = 12, message = "Card expiration month must be between 1 and 12")
    private Integer cardExpMonth;

    @NotNull(message = "Card expiration year is required")
    @Min(value = 2024, message = "Card expiration year must be valid")
    private Integer cardExpYear;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Builder.Default
    private Boolean autoRenewEnabled = true;
}

