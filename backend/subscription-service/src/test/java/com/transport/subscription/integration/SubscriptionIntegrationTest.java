package com.transport.subscription.integration;

import com.transport.subscription.entity.Subscription;
import com.transport.subscription.entity.SubscriptionPlan;
import com.transport.subscription.entity.enums.SubscriptionStatus;
import com.transport.subscription.repository.SubscriptionPlanRepository;
import com.transport.subscription.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration pour les repositories et entités
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SubscriptionIntegrationTest {

    @Autowired
    private SubscriptionPlanRepository planRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private SubscriptionPlan testPlan;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        testPlan = SubscriptionPlan.builder()
                .planCode("TEST_PLAN_" + System.currentTimeMillis())
                .description("Test plan")
                .durationDays(30)
                .price(new BigDecimal("100.00"))
                .currency("MAD")
                .isActive(true)
                .build();
        
        testPlan = planRepository.save(testPlan);
    }

    @Test
    void testCreateAndFindPlan() {
        // Given
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .planCode("MONTHLY_TEST")
                .description("Monthly plan")
                .durationDays(30)
                .price(new BigDecimal("200.00"))
                .currency("MAD")
                .isActive(true)
                .build();

        // When
        SubscriptionPlan saved = planRepository.save(plan);
        Optional<SubscriptionPlan> found = planRepository.findById(saved.getPlanId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getPlanCode()).isEqualTo("MONTHLY_TEST");
        assertThat(found.get().getPrice()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    void testCreateAndFindSubscription() {
        // Given
        Subscription subscription = Subscription.builder()
                .userId(testUserId)
                .plan(testPlan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .nextBillingDate(LocalDate.now().plusDays(30))
                .amountPaid(new BigDecimal("100.00"))
                .autoRenewEnabled(true)
                .build();

        // When
        Subscription saved = subscriptionRepository.save(subscription);
        Optional<Subscription> found = subscriptionRepository.findBySubscriptionIdAndDeletedAtIsNull(saved.getSubscriptionId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(testUserId);
        assertThat(found.get().getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    void testFindSubscriptionsByUserId() {
        // Given
        Subscription sub1 = Subscription.builder()
                .userId(testUserId)
                .plan(testPlan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .amountPaid(new BigDecimal("100.00"))
                .autoRenewEnabled(true)
                .build();

        Subscription sub2 = Subscription.builder()
                .userId(testUserId)
                .plan(testPlan)
                .status(SubscriptionStatus.PENDING)
                .startDate(LocalDate.now())
                .amountPaid(BigDecimal.ZERO)
                .autoRenewEnabled(true)
                .build();

        subscriptionRepository.save(sub1);
        subscriptionRepository.save(sub2);

        // When
        List<Subscription> found = subscriptionRepository.findByUserId(testUserId);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Subscription::getUserId).containsOnly(testUserId);
    }

    @Test
    void testFindByStatusAndAutoRenewEnabled() {
        // Given
        Subscription activeRenew = Subscription.builder()
                .userId(testUserId)
                .plan(testPlan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .nextBillingDate(LocalDate.now().plusDays(1))
                .amountPaid(new BigDecimal("100.00"))
                .autoRenewEnabled(true)
                .build();

        Subscription activeNoRenew = Subscription.builder()
                .userId(UUID.randomUUID())
                .plan(testPlan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .amountPaid(new BigDecimal("100.00"))
                .autoRenewEnabled(false)
                .build();

        subscriptionRepository.save(activeRenew);
        subscriptionRepository.save(activeNoRenew);

        // When
        List<Subscription> found = subscriptionRepository.findByStatusAndAutoRenewEnabled(
                SubscriptionStatus.ACTIVE, true);

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getAutoRenewEnabled()).isTrue();
    }
}

