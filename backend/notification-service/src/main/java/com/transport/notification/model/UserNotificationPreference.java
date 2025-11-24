package com.transport.notification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

/**
 * Entity representing user notification preferences.
 * 
 * <p>Stores user-specific notification settings including:
 * <ul>
 *   <li>Channel preferences (email, SMS, push)</li>
 *   <li>Contact information (email address, phone number)</li>
 *   <li>Push notification tokens</li>
 *   <li>Do-not-disturb time windows</li>
 * </ul>
 */
@Entity
@Table(name = "user_notification_preference", indexes = {
    @Index(name = "idx_preference_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preference_id", updatable = false, nullable = false)
    private Integer preferenceId;

    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    @Column(name = "email_enabled", nullable = false)
    @Builder.Default
    private Boolean emailEnabled = true;

    @Column(name = "email_address", length = 320)
    private String emailAddress;

    @Column(name = "sms_enabled", nullable = false)
    @Builder.Default
    private Boolean smsEnabled = false;

    @Column(name = "phone_number", length = 32)
    private String phoneNumber;

    @Column(name = "push_enabled", nullable = false)
    @Builder.Default
    private Boolean pushEnabled = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "push_tokens", columnDefinition = "jsonb")
    private String pushTokens; // JSON array of push tokens

    @Column(name = "do_not_disturb_start")
    private OffsetDateTime doNotDisturbStart;

    @Column(name = "do_not_disturb_end")
    private OffsetDateTime doNotDisturbEnd;

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}

