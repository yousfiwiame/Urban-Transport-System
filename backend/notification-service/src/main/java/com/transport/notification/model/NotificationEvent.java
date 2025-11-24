package com.transport.notification.model;

import com.transport.notification.model.enums.ProcessingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

/**
 * Entity representing an incoming event from other microservices.
 * 
 * <p>This entity tracks events received via Kafka from other services
 * (user-service, ticket-service, schedule-service, etc.) that trigger
 * notifications to be sent.
 */
@Entity
@Table(name = "notification_event", indexes = {
    @Index(name = "idx_event_type", columnList = "event_type"),
    @Index(name = "idx_event_correlation", columnList = "correlation_id"),
    @Index(name = "idx_event_status", columnList = "processing_status"),
    @Index(name = "idx_event_source", columnList = "source_service")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", updatable = false, nullable = false)
    private Integer eventId;

    @Column(name = "event_type", nullable = false, length = 64)
    private String eventType;

    @Column(name = "source_service", nullable = false, length = 64)
    private String sourceService;

    @Column(name = "correlation_id", length = 128)
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "processing_status", nullable = false, length = 16)
    @Builder.Default
    private ProcessingStatus processingStatus = ProcessingStatus.RECEIVED;

    @Column(name = "received_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime receivedAt = OffsetDateTime.now();

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;
}

