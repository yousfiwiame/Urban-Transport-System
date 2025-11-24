package com.transport.notification.model;

import com.transport.notification.model.enums.ChannelStatus;
import com.transport.notification.model.enums.ChannelType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

/**
 * Entity representing channel-specific delivery tracking for notifications.
 * 
 * <p>This entity tracks delivery attempts for each notification channel
 * (EMAIL, SMS, PUSH) separately, including retry logic, error tracking,
 * and delivery status.
 */
@Entity
@Table(name = "notification_channel", indexes = {
    @Index(name = "idx_channel_notification", columnList = "notification_id"),
    @Index(name = "idx_channel_status", columnList = "channel_status"),
    @Index(name = "idx_channel_type", columnList = "channel_type"),
    @Index(name = "idx_channel_next_retry", columnList = "next_retry_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_id", updatable = false, nullable = false)
    private Integer channelId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "channel_type", nullable = false, length = 16)
    private ChannelType channelType;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "channel_status", nullable = false, length = 16)
    @Builder.Default
    private ChannelStatus channelStatus = ChannelStatus.PENDING;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "last_attempt_at")
    private OffsetDateTime lastAttemptAt;

    @Column(name = "next_retry_at")
    private OffsetDateTime nextRetryAt;

    @Column(name = "error_code", length = 64)
    private String errorCode;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false, foreignKey = @ForeignKey(name = "fk_channel_notification"))
    private Notification notification;
}

