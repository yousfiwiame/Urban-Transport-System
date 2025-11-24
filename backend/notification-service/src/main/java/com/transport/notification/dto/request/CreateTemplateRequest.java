package com.transport.notification.dto.request;

import com.transport.notification.model.enums.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a notification template.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTemplateRequest {

    @NotBlank(message = "Template code is required")
    @Size(max = 50, message = "Template code must not exceed 50 characters")
    private String templateCode;

    @NotNull(message = "Channel type is required")
    private ChannelType channelType;

    @Size(max = 255, message = "Subject must not exceed 255 characters")
    private String subject;

    @NotBlank(message = "Body template is required")
    private String bodyTemplate;

    @Builder.Default
    private Boolean isActive = true;
}

