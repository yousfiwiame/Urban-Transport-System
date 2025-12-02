package com.transport.notification.dto.mapper;

import com.transport.notification.dto.request.CreateTemplateRequest;
import com.transport.notification.dto.request.SendNotificationRequest;
import com.transport.notification.dto.request.UpdatePreferenceRequest;
import com.transport.notification.dto.response.NotificationResponse;
import com.transport.notification.dto.response.PreferenceResponse;
import com.transport.notification.dto.response.TemplateResponse;
import com.transport.notification.model.Notification;
import com.transport.notification.model.NotificationTemplate;
import com.transport.notification.model.UserNotificationPreference;
import com.transport.notification.model.enums.NotificationStatus;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for converting between entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    // Notification mappings
    @Mapping(target = "notificationId", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "scheduledAt", source = "scheduledAt")
    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "readAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "template", ignore = true)
    @Mapping(target = "channels", ignore = true)
    @Mapping(target = "logs", ignore = true)
    Notification toEntity(SendNotificationRequest request);

    @Mapping(source = "event.eventId", target = "eventId")
    @Mapping(source = "template.templateId", target = "templateId")
    @Mapping(target = "channel", expression = "java(getFirstChannelType(notification))")
    NotificationResponse toResponse(Notification notification);

    default com.transport.notification.model.enums.ChannelType getFirstChannelType(Notification notification) {
        if (notification.getChannels() != null && !notification.getChannels().isEmpty()) {
            return notification.getChannels().get(0).getChannelType();
        }
        return null;
    }

    List<NotificationResponse> toResponseList(List<Notification> notifications);

    // Template mappings
    @Mapping(target = "templateId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    NotificationTemplate toEntity(CreateTemplateRequest request);

    TemplateResponse toResponse(NotificationTemplate template);

    List<TemplateResponse> toTemplateResponseList(List<NotificationTemplate> templates);

    // Preference mappings
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "preferenceId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "pushTokens", expression = "java(serializePushTokens(request.getPushTokens()))")
    void updatePreferenceFromRequest(UpdatePreferenceRequest request, @MappingTarget UserNotificationPreference preference);

    @Mapping(target = "pushTokens", expression = "java(parsePushTokens(preference.getPushTokens()))")
    PreferenceResponse toResponse(UserNotificationPreference preference);

    default List<String> parsePushTokens(String jsonTokens) {
        // Simple JSON parsing - in production, use proper JSON library like Jackson
        if (jsonTokens == null || jsonTokens.isEmpty() || "null".equals(jsonTokens)) {
            return List.of();
        }
        // This is a placeholder - actual implementation would parse JSON array
        // For now, return empty list
        return List.of();
    }

    default String serializePushTokens(List<String> pushTokens) {
        // Simple JSON serialization - in production, use proper JSON library like Jackson
        if (pushTokens == null || pushTokens.isEmpty()) {
            return null;
        }
        // This is a placeholder - actual implementation would serialize to JSON array
        // For now, return null
        return null;
    }
}

