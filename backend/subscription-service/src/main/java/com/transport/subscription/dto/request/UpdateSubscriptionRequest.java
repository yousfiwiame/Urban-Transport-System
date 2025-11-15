package com.transport.subscription.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSubscriptionRequest {

    private Boolean autoRenewEnabled;

    private String cardToken;

    private Integer cardExpMonth;

    private Integer cardExpYear;
}

