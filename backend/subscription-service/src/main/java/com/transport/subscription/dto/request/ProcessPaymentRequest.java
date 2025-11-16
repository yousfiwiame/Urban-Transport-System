package com.transport.subscription.dto.request;

import com.transport.subscription.entity.enums.PaymentMethod;
import com.transport.subscription.entity.enums.PaymentType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessPaymentRequest {

    @NotNull(message = "Subscription ID is required")
    private UUID subscriptionId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    @Pattern(regexp = "[A-Z]{3}", message = "Currency must be a valid ISO 4217 code (3 uppercase letters)")
    private String currency;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotBlank(message = "Card token is required")
    @Size(max = 128, message = "Card token must not exceed 128 characters")
    private String cardToken;

    @NotBlank(message = "Idempotency key is required")
    @Size(max = 128, message = "Idempotency key must not exceed 128 characters")
    private String idempotencyKey;

    @Builder.Default
    private PaymentType paymentType = PaymentType.INITIAL;
}

