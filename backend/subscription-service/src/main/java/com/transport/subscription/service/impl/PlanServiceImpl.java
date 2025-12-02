package com.transport.subscription.service.impl;

import com.transport.subscription.dto.mapper.PlanMapper;
import com.transport.subscription.dto.request.CreatePlanRequest;
import com.transport.subscription.dto.response.PlanResponse;
import com.transport.subscription.entity.SubscriptionPlan;
import com.transport.subscription.exception.PlanNotFoundException;
import com.transport.subscription.repository.SubscriptionPlanRepository;
import com.transport.subscription.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlanServiceImpl implements PlanService {

    private final SubscriptionPlanRepository planRepository;
    private final PlanMapper planMapper;

    @Override
    public PlanResponse createPlan(CreatePlanRequest request) {
        log.info("Creating plan with code: {}", request.getPlanCode());

        if (planRepository.existsByPlanCode(request.getPlanCode())) {
            throw new PlanNotFoundException("Plan with code already exists: " + request.getPlanCode());
        }

        SubscriptionPlan plan = planMapper.toEntity(request);
        plan = planRepository.save(plan);

        log.info("Plan created successfully: {}", plan.getPlanId());
        return planMapper.toResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponse getPlanById(UUID planId) {
        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException("Plan not found: " + planId));
        return planMapper.toResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponse getPlanByCode(String planCode) {
        SubscriptionPlan plan = planRepository.findByPlanCode(planCode)
                .orElseThrow(() -> new PlanNotFoundException("Plan not found with code: " + planCode));
        return planMapper.toResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> getAllPlans() {
        List<SubscriptionPlan> plans = planRepository.findAll();
        return planMapper.toResponseList(plans);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> getActivePlans() {
        List<SubscriptionPlan> plans = planRepository.findByIsActiveTrue();
        return planMapper.toResponseList(plans);
    }

    @Override
    public PlanResponse updatePlan(UUID planId, CreatePlanRequest request) {
        log.info("Updating plan: {}", planId);

        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException("Plan not found: " + planId));

        planMapper.updateEntityFromRequest(request, plan);
        plan = planRepository.save(plan);

        log.info("Plan updated successfully: {}", planId);
        return planMapper.toResponse(plan);
    }

    @Override
    public void deletePlan(UUID planId) {
        log.info("Deleting plan: {}", planId);

        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException("Plan not found: " + planId));

        plan.setIsActive(false);
        planRepository.save(plan);

        log.info("Plan deactivated: {}", planId);
    }
}

