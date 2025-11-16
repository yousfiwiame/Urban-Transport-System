package com.transport.subscription.service;

import com.transport.subscription.dto.request.CreatePlanRequest;
import com.transport.subscription.dto.response.PlanResponse;

import java.util.List;
import java.util.UUID;

public interface PlanService {
    PlanResponse createPlan(CreatePlanRequest request);
    PlanResponse getPlanById(UUID planId);
    PlanResponse getPlanByCode(String planCode);
    List<PlanResponse> getAllPlans();
    List<PlanResponse> getActivePlans();
    PlanResponse updatePlan(UUID planId, CreatePlanRequest request);
    void deletePlan(UUID planId);
}

