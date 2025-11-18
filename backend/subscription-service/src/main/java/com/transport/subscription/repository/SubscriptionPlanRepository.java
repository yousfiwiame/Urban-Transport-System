package com.transport.subscription.repository;

import com.transport.subscription.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Integer> {

    Optional<SubscriptionPlan> findByPlanCode(String planCode);

    List<SubscriptionPlan> findByIsActiveTrue();

    boolean existsByPlanCode(String planCode);

    List<SubscriptionPlan> findByIsActive(Boolean isActive);
}

