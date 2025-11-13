package com.transport.subscription.dto.mapper;

import com.transport.subscription.dto.request.CreateSubscriptionRequest;
import com.transport.subscription.dto.request.UpdateSubscriptionRequest;
import com.transport.subscription.dto.response.SubscriptionResponse;
import com.transport.subscription.entity.Subscription;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PlanMapper.class})
public interface SubscriptionMapper {

    @Mapping(target = "subscriptionId", ignore = true)
    @Mapping(target = "plan", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "nextBillingDate", ignore = true)
    @Mapping(target = "amountPaid", ignore = true)
    @Mapping(target = "qrCodeData", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "history", ignore = true)
    Subscription toEntity(CreateSubscriptionRequest request);

    @Mapping(target = "hasQrCode", expression = "java(subscription.getQrCodeData() != null && !subscription.getQrCodeData().isEmpty())")
    SubscriptionResponse toResponse(Subscription subscription);

    List<SubscriptionResponse> toResponseList(List<Subscription> subscriptions);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "subscriptionId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "plan", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "nextBillingDate", ignore = true)
    @Mapping(target = "amountPaid", ignore = true)
    @Mapping(target = "qrCodeData", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "history", ignore = true)
    void updateEntityFromRequest(UpdateSubscriptionRequest request, @MappingTarget Subscription subscription);
}

