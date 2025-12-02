package com.transport.subscription.unit;

import com.transport.subscription.dto.request.CreatePlanRequest;
import com.transport.subscription.dto.response.PlanResponse;
import com.transport.subscription.entity.SubscriptionPlan;
import com.transport.subscription.exception.PlanNotFoundException;
import com.transport.subscription.repository.SubscriptionPlanRepository;
import com.transport.subscription.service.impl.PlanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Plan Service Unit Tests")
class PlanServiceTest {

    @Mock
    private SubscriptionPlanRepository planRepository;

    @Mock
    private com.transport.subscription.dto.mapper.PlanMapper planMapper;

    @InjectMocks
    private PlanServiceImpl planService;

    private SubscriptionPlan testPlan;
    private CreatePlanRequest createRequest;
    private PlanResponse planResponse;

    @BeforeEach
    void setUp() {
        UUID planId = UUID.randomUUID();
        
        testPlan = SubscriptionPlan.builder()
                .planId(planId)
                .planCode("MONTHLY")
                .description("Monthly subscription")
                .durationDays(30)
                .price(new BigDecimal("200.00"))
                .currency("MAD")
                .isActive(true)
                .build();

        createRequest = CreatePlanRequest.builder()
                .planCode("MONTHLY")
                .description("Monthly subscription")
                .durationDays(30)
                .price(new BigDecimal("200.00"))
                .currency("MAD")
                .build();

        planResponse = PlanResponse.builder()
                .planId(planId)
                .planCode("MONTHLY")
                .description("Monthly subscription")
                .durationDays(30)
                .price(new BigDecimal("200.00"))
                .currency("MAD")
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Should create plan successfully")
    void testCreatePlan_Success() {
        // Given
        when(planRepository.existsByPlanCode("MONTHLY")).thenReturn(false);
        when(planMapper.toEntity(createRequest)).thenReturn(testPlan);
        when(planRepository.save(any(SubscriptionPlan.class))).thenReturn(testPlan);
        when(planMapper.toResponse(testPlan)).thenReturn(planResponse);

        // When
        PlanResponse response = planService.createPlan(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPlanCode()).isEqualTo("MONTHLY");
        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("200.00"));
        verify(planRepository).save(any(SubscriptionPlan.class));
    }

    @Test
    @DisplayName("Should throw exception when plan code already exists")
    void testCreatePlan_DuplicateCode() {
        // Given
        when(planRepository.existsByPlanCode("MONTHLY")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> planService.createPlan(createRequest))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should get plan by ID successfully")
    void testGetPlanById_Success() {
        // Given
        UUID planId = testPlan.getPlanId();
        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(planMapper.toResponse(testPlan)).thenReturn(planResponse);

        // When
        PlanResponse response = planService.getPlanById(planId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPlanId()).isEqualTo(planId);
        verify(planRepository).findById(planId);
    }

    @Test
    @DisplayName("Should throw exception when plan not found")
    void testGetPlanById_NotFound() {
        // Given
        UUID planId = UUID.randomUUID();
        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> planService.getPlanById(planId))
                .isInstanceOf(PlanNotFoundException.class);
    }

    @Test
    @DisplayName("Should get all plans successfully")
    void testGetAllPlans_Success() {
        // Given
        List<SubscriptionPlan> plans = Arrays.asList(testPlan);
        List<PlanResponse> responses = Arrays.asList(planResponse);
        when(planRepository.findAll()).thenReturn(plans);
        when(planMapper.toResponseList(plans)).thenReturn(responses);

        // When
        List<PlanResponse> result = planService.getAllPlans();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlanCode()).isEqualTo("MONTHLY");
    }

    @Test
    @DisplayName("Should deactivate plan successfully")
    void testDeletePlan_Success() {
        // Given
        UUID planId = testPlan.getPlanId();
        when(planRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        when(planRepository.save(any(SubscriptionPlan.class))).thenReturn(testPlan);

        // When
        planService.deletePlan(planId);

        // Then
        verify(planRepository).findById(planId);
        verify(planRepository).save(any(SubscriptionPlan.class));
        assertThat(testPlan.getIsActive()).isFalse();
    }
}

