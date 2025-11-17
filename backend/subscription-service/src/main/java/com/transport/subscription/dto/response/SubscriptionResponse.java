package com.transport.subscription.dto.response;

import com.transport.subscription.entity.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private Integer subscriptionId;
    private Integer userId;
    private PlanResponse plan;
    private Integer planId;
    private String planCode;
    private String currency;
    private SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextBillingDate;
    private BigDecimal amountPaid;
    private Boolean autoRenewEnabled;
    private Boolean hasQrCode;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

