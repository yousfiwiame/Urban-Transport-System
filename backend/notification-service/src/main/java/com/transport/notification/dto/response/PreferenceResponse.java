package com.transport.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Response DTO for user notification preferences.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenceResponse {

    private Integer preferenceId;
    private Integer userId;
    private Boolean emailEnabled;
    private String emailAddress;
    private Boolean smsEnabled;
    private String phoneNumber;
    private Boolean pushEnabled;
    private List<String> pushTokens;
    private OffsetDateTime doNotDisturbStart;
    private OffsetDateTime doNotDisturbEnd;
    private OffsetDateTime updatedAt;
}

