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
 * Entity representing audit logs for notification actions.
 * 
 * <p>This entity tracks all actions performed on notifications for
 * auditing and debugging purposes. Each log entry contains the action
 * type, timestamp, and optional metadata in JSON format.
 */
@Entity
@Table(name = "notification_log", indexes = {
    @Index(name = "idx_log_notification", columnList = "notification_id"),
    @Index(name = "idx_log_logged_at", columnList = "logged_at"),
    @Index(name = "idx_log_action", columnList = "action")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", updatable = false, nullable = false)
    private Integer logId;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "logged_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime loggedAt = OffsetDateTime.now();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // JSON object with additional log data

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false, foreignKey = @ForeignKey(name = "fk_log_notification"))
    private Notification notification;
}

