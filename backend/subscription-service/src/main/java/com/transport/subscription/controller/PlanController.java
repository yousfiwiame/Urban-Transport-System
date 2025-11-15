package com.transport.subscription.controller;

import com.transport.subscription.dto.request.CreatePlanRequest;
import com.transport.subscription.dto.response.PlanResponse;
import com.transport.subscription.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Tag(name = "Plan", description = "Subscription plan management APIs")
public class PlanController {

    private final PlanService planService;

    @PostMapping
    @Operation(summary = "Create a new subscription plan")
    public ResponseEntity<PlanResponse> createPlan(@Valid @RequestBody CreatePlanRequest request) {
        log.info("Creating plan with code: {}", request.getPlanCode());
        PlanResponse response = planService.createPlan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all subscription plans")
    public ResponseEntity<List<PlanResponse>> getAllPlans() {
        List<PlanResponse> responses = planService.getAllPlans();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active subscription plans")
    public ResponseEntity<List<PlanResponse>> getActivePlans() {
        List<PlanResponse> responses = planService.getActivePlans();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get plan by ID")
    public ResponseEntity<PlanResponse> getPlanById(@PathVariable UUID id) {
        PlanResponse response = planService.getPlanById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get plan by code")
    public ResponseEntity<PlanResponse> getPlanByCode(@PathVariable String code) {
        PlanResponse response = planService.getPlanByCode(code);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a subscription plan")
    public ResponseEntity<PlanResponse> updatePlan(
            @PathVariable UUID id,
            @Valid @RequestBody CreatePlanRequest request) {
        PlanResponse response = planService.updatePlan(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete (deactivate) a subscription plan")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Plan successfully deactivated"
    )
    public ResponseEntity<Void> deletePlan(@PathVariable UUID id) {
        log.info("Deleting plan: {}", id);
        planService.deletePlan(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}

