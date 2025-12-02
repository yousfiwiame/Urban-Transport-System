package com.transport.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.transport.notification.model.enums.ChannelType;
import com.transport.notification.model.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Response DTO for notification information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    @JsonProperty("id")
    private Integer notificationId;

    @JsonProperty("recipientId")
    private Integer userId;

    private String title;

    @JsonProperty("message")
    private String messageBody;

    private NotificationStatus status;
    private ChannelType channel;
    private OffsetDateTime scheduledAt;
    private OffsetDateTime sentAt;
    private OffsetDateTime readAt;
    private OffsetDateTime createdAt;
    private Integer eventId;
    private Integer templateId;
}

