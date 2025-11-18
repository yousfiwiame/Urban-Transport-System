package com.transport.subscription.dto.response;

import com.transport.subscription.entity.enums.PaymentMethod;
import com.transport.subscription.entity.enums.PaymentStatus;
import com.transport.subscription.entity.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Integer paymentId;
    private Integer subscriptionId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private PaymentType paymentType;
    private OffsetDateTime paymentDate;
    private String failureReason;
    private String externalTxnId;
    private OffsetDateTime createdAt;
}

