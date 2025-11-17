package com.transport.subscription.entity;

import com.transport.subscription.entity.enums.SubscriptionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "subscription_history", indexes = {
    @Index(name = "idx_history_subscription", columnList = "subscription_id"),
    @Index(name = "idx_history_event_date", columnList = "event_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id", updatable = false, nullable = false)
    private Integer historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false, foreignKey = @ForeignKey(name = "fk_history_subscription"))
    @NotNull
    private Subscription subscription;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "old_status", length = 20)
    private SubscriptionStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "new_status", nullable = false, length = 20)
    @NotNull
    private SubscriptionStatus newStatus;

    @Column(name = "event_type", nullable = false, length = 64)
    @NotNull
    private String eventType;

    @Column(name = "event_date", nullable = false)
    @Builder.Default
    private OffsetDateTime eventDate = OffsetDateTime.now();

    @Column(name = "performed_by")
    private Integer performedBy;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;
}

