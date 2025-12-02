package com.transport.notification.dto.response;

import com.transport.notification.model.enums.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Response DTO for notification template information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateResponse {

    private Integer templateId;
    private String templateCode;
    private ChannelType channelType;
    private String subject;
    private String bodyTemplate;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

