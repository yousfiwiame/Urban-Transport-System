package com.transport.notification.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Request DTO for updating user notification preferences.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePreferenceRequest {

    private Boolean emailEnabled;

    @Email(message = "Email address must be valid")
    @Size(max = 320, message = "Email address must not exceed 320 characters")
    private String emailAddress;

    private Boolean smsEnabled;

    @Size(max = 32, message = "Phone number must not exceed 32 characters")
    private String phoneNumber;

    private Boolean pushEnabled;

    private List<String> pushTokens;

    private OffsetDateTime doNotDisturbStart;

    private OffsetDateTime doNotDisturbEnd;
}

