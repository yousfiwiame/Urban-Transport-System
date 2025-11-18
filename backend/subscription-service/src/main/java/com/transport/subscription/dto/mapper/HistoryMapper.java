package com.transport.subscription.dto.mapper;

import com.transport.subscription.dto.response.SubscriptionHistoryResponse;
import com.transport.subscription.entity.SubscriptionHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HistoryMapper {

    @Mapping(target = "subscriptionId", source = "subscription.subscriptionId")
    SubscriptionHistoryResponse toResponse(SubscriptionHistory history);

    List<SubscriptionHistoryResponse> toResponseList(List<SubscriptionHistory> histories);
}

