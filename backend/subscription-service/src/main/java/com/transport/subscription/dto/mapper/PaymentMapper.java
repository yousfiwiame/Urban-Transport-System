package com.transport.subscription.dto.mapper;

import com.transport.subscription.dto.response.PaymentResponse;
import com.transport.subscription.entity.SubscriptionPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "subscriptionId", source = "subscription.subscriptionId")
    PaymentResponse toResponse(SubscriptionPayment payment);

    List<PaymentResponse> toResponseList(List<SubscriptionPayment> payments);
}

