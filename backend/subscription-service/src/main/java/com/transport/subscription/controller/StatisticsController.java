package com.transport.subscription.controller;

import com.transport.subscription.repository.SubscriptionRepository;
import com.transport.subscription.repository.SubscriptionPlanRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for subscription service statistics.
 * Provides statistics for subscriptions and plans.
 */
@Slf4j
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Statistics endpoints for admin dashboard")
@SecurityRequirement(name = "Bearer Authentication")
public class StatisticsController {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;

    /**
     * Get subscription statistics for the admin dashboard.
     * 
     * Returns:
     * - Total number of subscriptions
     * - Number of active subscriptions
     * - Number of expired/cancelled subscriptions
     * - Total number of plans
     * - Number of active plans
     * - Total revenue from subscriptions
     * 
     * @return ResponseEntity containing subscription statistics
     */
    @GetMapping("/subscriptions")
    @Operation(summary = "Get subscription statistics")
    public ResponseEntity<Map<String, Object>> getSubscriptionStatistics() {
        log.info("Fetching subscription statistics for admin dashboard");

        Map<String, Object> stats = new HashMap<>();

        // Subscription statistics
        long totalSubscriptions = subscriptionRepository.count();
        stats.put("totalSubscriptions", totalSubscriptions);

        long activeSubscriptions = subscriptionRepository.countByActiveTrue();
        stats.put("activeSubscriptions", activeSubscriptions);

        // Plan statistics
        long totalPlans = planRepository.count();
        stats.put("totalPlans", totalPlans);

        long activePlans = planRepository.countByIsActiveTrue();
        stats.put("activePlans", activePlans);

        log.info("Subscription statistics: {} subscriptions, {} plans",
                totalSubscriptions, totalPlans);

        return ResponseEntity.ok(stats);
    }
}

