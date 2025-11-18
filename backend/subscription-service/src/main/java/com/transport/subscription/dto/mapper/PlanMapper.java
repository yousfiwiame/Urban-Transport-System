package com.transport.subscription.dto.mapper;

import com.transport.subscription.dto.request.CreatePlanRequest;
import com.transport.subscription.dto.response.PlanResponse;
import com.transport.subscription.entity.SubscriptionPlan;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    @Mapping(target = "planId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    SubscriptionPlan toEntity(CreatePlanRequest request);

    PlanResponse toResponse(SubscriptionPlan plan);

    List<PlanResponse> toResponseList(List<SubscriptionPlan> plans);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "planId", ignore = true)
    @Mapping(target = "planCode", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    void updateEntityFromRequest(CreatePlanRequest request, @MappingTarget SubscriptionPlan plan);
}

