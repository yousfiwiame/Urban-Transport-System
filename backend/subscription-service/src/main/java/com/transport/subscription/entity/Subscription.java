package com.transport.subscription.entity;

import com.transport.subscription.entity.enums.SubscriptionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subscription", indexes = {
    @Index(name = "idx_subscription_plan_id", columnList = "plan_id"),
    @Index(name = "idx_subscription_user_id", columnList = "user_id"),
    @Index(name = "idx_subscription_status", columnList = "status"),
    @Index(name = "idx_subscription_next_billing", columnList = "next_billing_date"),
    @Index(name = "idx_subscription_deleted_at", columnList = "deleted_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "subscription_id", updatable = false, nullable = false)
    private UUID subscriptionId;

    @Column(name = "user_id", nullable = false, updatable = false)
    @NotNull
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false, foreignKey = @ForeignKey(name = "fk_subscription_plan"))
    @NotNull
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.PENDING;

    @Column(name = "start_date", nullable = false)
    @NotNull
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "next_billing_date")
    private LocalDate nextBillingDate;

    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "auto_renew_enabled", nullable = false)
    @Builder.Default
    private Boolean autoRenewEnabled = true;

    @Column(name = "card_token", length = 128)
    private String cardToken;

    @Column(name = "card_exp_month")
    private Integer cardExpMonth;

    @Column(name = "card_exp_year")
    private Integer cardExpYear;

    @Column(name = "qr_code_data", columnDefinition = "TEXT")
    private String qrCodeData;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    @Builder.Default
    private List<SubscriptionPayment> payments = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SubscriptionHistory> history;

    @PrePersist
    @PreUpdate
    public void onPrePersistOrUpdate() {
        // Mise à jour du timestamp (seulement lors d'un UPDATE, pas lors d'un INSERT)
        // On vérifie si l'entité existe déjà (subscriptionId != null) pour savoir si c'est un UPDATE
        if (this.subscriptionId != null) {
            this.updatedAt = OffsetDateTime.now();
        }
        
        // Validation des dates
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalStateException("End date must be after start date");
        }
        
        // Validation du mois d'expiration de la carte
        if (cardExpMonth != null && (cardExpMonth < 1 || cardExpMonth > 12)) {
            throw new IllegalStateException("Card expiration month must be between 1 and 12");
        }
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = OffsetDateTime.now();
    }
}

