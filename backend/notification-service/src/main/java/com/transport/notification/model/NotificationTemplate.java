package com.transport.notification.model;

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
 * Entity representing a notification template.
 * 
 * <p>Templates define reusable notification formats for different channels
 * (EMAIL, SMS, PUSH, WEBHOOK) with placeholders that can be replaced with
 * actual values when sending notifications.
 */
@Entity
@Table(name = "notification_template", indexes = {
    @Index(name = "idx_template_code", columnList = "template_code"),
    @Index(name = "idx_template_channel", columnList = "channel_type"),
    @Index(name = "idx_template_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id", updatable = false, nullable = false)
    private Integer templateId;

    @Column(name = "template_code", nullable = false, unique = true, length = 50)
    private String templateCode;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "channel_type", nullable = false, length = 20)
    private ChannelType channelType;

    @Column(name = "subject", length = 255)
    private String subject;

    @Column(name = "body_template", nullable = false, columnDefinition = "TEXT")
    private String bodyTemplate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}

