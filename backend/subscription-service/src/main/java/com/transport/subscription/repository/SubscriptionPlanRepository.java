package com.transport.subscription.repository;

import com.transport.subscription.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {

    Optional<SubscriptionPlan> findByPlanCode(String planCode);

    List<SubscriptionPlan> findByIsActiveTrue();

    boolean existsByPlanCode(String planCode);

    List<SubscriptionPlan> findByIsActive(Boolean isActive);

    /**
     * Counts active plans.
     * Used for admin dashboard statistics.
     * 
     * @return the number of active plans
     */
    long countByIsActiveTrue();
}

