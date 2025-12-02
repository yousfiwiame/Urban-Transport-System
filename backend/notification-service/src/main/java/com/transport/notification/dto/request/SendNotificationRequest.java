package com.transport.notification.dto.request;

import com.transport.notification.model.enums.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Request DTO for sending a notification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendNotificationRequest {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Message body is required")
    private String messageBody;

    @NotNull(message = "Channel type is required")
    private ChannelType channelType;

    private String templateCode;

    private Map<String, String> templateVariables;

    private OffsetDateTime scheduledAt;

    private String correlationId;
}

