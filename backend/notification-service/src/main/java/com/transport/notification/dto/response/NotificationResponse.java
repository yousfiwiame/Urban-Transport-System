package com.transport.notification.dto.response;

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

    private Integer notificationId;
    private Integer userId;
    private String title;
    private String messageBody;
    private NotificationStatus status;
    private OffsetDateTime scheduledAt;
    private OffsetDateTime sentAt;
    private OffsetDateTime readAt;
    private OffsetDateTime createdAt;
    private Integer eventId;
    private Integer templateId;
}

