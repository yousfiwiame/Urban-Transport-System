package com.transport.notification.controller;

import com.transport.notification.dto.request.UpdatePreferenceRequest;
import com.transport.notification.dto.response.PreferenceResponse;
import com.transport.notification.model.UserNotificationPreference;
import com.transport.notification.repository.NotificationPreferenceRepository;
import com.transport.notification.dto.mapper.NotificationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user notification preferences.
 */
@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Preference Management", description = "User notification preference management endpoints")
public class PreferenceController {

    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationMapper notificationMapper;

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get notification preferences for a user")
    public ResponseEntity<PreferenceResponse> getPreferences(@PathVariable Integer userId) {
        UserNotificationPreference preference = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Create default preferences if not found
                    UserNotificationPreference newPreference = UserNotificationPreference.builder()
                            .userId(userId)
                            .emailEnabled(true)
                            .smsEnabled(false)
                            .pushEnabled(false)
                            .build();
                    return preferenceRepository.save(newPreference);
                });
        
        return ResponseEntity.ok(notificationMapper.toResponse(preference));
    }

    @PutMapping("/users/{userId}")
    @Operation(summary = "Update notification preferences for a user")
    public ResponseEntity<PreferenceResponse> updatePreferences(
            @PathVariable Integer userId,
            @Valid @RequestBody UpdatePreferenceRequest request) {
        
        UserNotificationPreference preference = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserNotificationPreference newPreference = UserNotificationPreference.builder()
                            .userId(userId)
                            .build();
                    return preferenceRepository.save(newPreference);
                });
        
        notificationMapper.updatePreferenceFromRequest(request, preference);
        preference.setUpdatedAt(java.time.OffsetDateTime.now());
        preference = preferenceRepository.save(preference);
        
        return ResponseEntity.ok(notificationMapper.toResponse(preference));
    }
}

