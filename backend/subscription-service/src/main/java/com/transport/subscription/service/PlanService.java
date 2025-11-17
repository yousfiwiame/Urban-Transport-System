package com.transport.subscription.service;

import com.transport.subscription.dto.request.CreatePlanRequest;
import com.transport.subscription.dto.response.PlanResponse;

import java.util.List;

public interface PlanService {
    PlanResponse createPlan(CreatePlanRequest request);
    PlanResponse getPlanById(Integer planId);
    PlanResponse getPlanByCode(String planCode);
    List<PlanResponse> getAllPlans();
    List<PlanResponse> getActivePlans();
    PlanResponse updatePlan(Integer planId, CreatePlanRequest request);
    void deletePlan(Integer planId);
}

