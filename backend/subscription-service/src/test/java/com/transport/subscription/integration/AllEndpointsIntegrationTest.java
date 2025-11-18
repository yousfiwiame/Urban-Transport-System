package com.transport.subscription.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.subscription.dto.request.CancelSubscriptionRequest;
import com.transport.subscription.dto.request.CreatePlanRequest;
import com.transport.subscription.dto.request.CreateSubscriptionRequest;
import com.transport.subscription.dto.request.ProcessPaymentRequest;
import com.transport.subscription.dto.request.RenewSubscriptionRequest;
import com.transport.subscription.dto.request.UpdateSubscriptionRequest;
import com.transport.subscription.dto.response.PlanResponse;
import com.transport.subscription.dto.response.SubscriptionResponse;
import com.transport.subscription.entity.enums.PaymentMethod;
import com.transport.subscription.entity.enums.SubscriptionStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration automatisés pour TOUS les endpoints
 * Exécute tous les tests en séquence pour valider le fonctionnement complet
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class AllEndpointsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    // Données de test réutilisables
    private static Integer testPlanId;
    private static Integer testSubscriptionId;
    private static Integer testUserId;
    private static String testPlanCode = "TEST_MONTHLY_" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        testUserId = 100;
    }

    @Test
    @Order(1)
    @DisplayName("✅ TEST 1: Create Plan - POST /api/subscriptions/plans")
    void test1_CreatePlan() throws Exception {
        CreatePlanRequest request = CreatePlanRequest.builder()
                .planCode(testPlanCode)
                .description("Monthly subscription plan for testing")
                .durationDays(30)
                .price(new BigDecimal("200.00"))
                .currency("MAD")
                .build();

        String response = mockMvc.perform(post("/api/subscriptions/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.planId").exists())
                .andExpect(jsonPath("$.planCode").value(testPlanCode))
                .andExpect(jsonPath("$.price").value(200.00))
                .andExpect(jsonPath("$.currency").value("MAD"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PlanResponse planResponse = objectMapper.readValue(response, PlanResponse.class);
        testPlanId = planResponse.getPlanId();
        System.out.println("✅ Plan créé avec ID: " + testPlanId);
    }

    @Test
    @Order(2)
    @DisplayName("✅ TEST 2: Get Plan by ID - GET /api/subscriptions/plans/{id}")
    void test2_GetPlanById() throws Exception {
        mockMvc.perform(get("/api/subscriptions/plans/{id}", testPlanId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planId").value(testPlanId))
                .andExpect(jsonPath("$.planCode").value(testPlanCode))
                .andExpect(jsonPath("$.price").value(200.00));
    }

    @Test
    @Order(3)
    @DisplayName("✅ TEST 3: Get All Plans - GET /api/subscriptions/plans")
    void test3_GetAllPlans() throws Exception {
        mockMvc.perform(get("/api/subscriptions/plans"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].planId").exists())
                .andExpect(jsonPath("$[0].planCode").exists());
    }

    @Test
    @Order(4)
    @DisplayName("✅ TEST 4: Create Subscription - POST /api/subscriptions")
    void test4_CreateSubscription() throws Exception {
        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder()
                .userId(testUserId)
                .planId(testPlanId)
                .cardToken("tok_visa_test")
                .cardExpMonth(12)
                .cardExpYear(2025)
                .paymentMethod(PaymentMethod.CARD)
                .autoRenewEnabled(true)
                .build();

        String response = mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subscriptionId").exists())
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.planId").value(testPlanId))
                .andExpect(jsonPath("$.status").value(anyOf(
                        is(SubscriptionStatus.ACTIVE.name()),
                        is(SubscriptionStatus.PENDING.name()))))
                .andReturn()
                .getResponse()
                .getContentAsString();

        SubscriptionResponse subscriptionResponse = objectMapper.readValue(response, SubscriptionResponse.class);
        testSubscriptionId = subscriptionResponse.getSubscriptionId();
        System.out.println("✅ Subscription créée avec ID: " + testSubscriptionId);
    }

    @Test
    @Order(5)
    @DisplayName("✅ TEST 5: Get Subscription by ID - GET /api/subscriptions/{id}")
    void test5_GetSubscriptionById() throws Exception {
        mockMvc.perform(get("/api/subscriptions/{id}", testSubscriptionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptionId").value(testSubscriptionId))
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.planId").value(testPlanId));
    }

    @Test
    @Order(6)
    @DisplayName("✅ TEST 6: Get Subscriptions by User - GET /api/subscriptions/user/{userId}")
    void test6_GetSubscriptionsByUser() throws Exception {
        mockMvc.perform(get("/api/subscriptions/user/{userId}", testUserId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].subscriptionId").exists())
                .andExpect(jsonPath("$[0].userId").value(testUserId));
    }

    @Test
    @Order(7)
    @DisplayName("✅ TEST 7: Get QR Code - GET /api/subscriptions/{id}/qrcode")
    void test7_GetQRCode() throws Exception {
        mockMvc.perform(get("/api/subscriptions/{id}/qrcode", testSubscriptionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptionId").value(testSubscriptionId))
                .andExpect(jsonPath("$.qrCodeData").exists());
    }

    @Test
    @Order(8)
    @DisplayName("✅ TEST 8: Validate QR Code - POST /api/subscriptions/validate-qrcode")
    void test8_ValidateQRCode() throws Exception {
        // D'abord récupérer le QR code
        String qrCodeResponse = mockMvc.perform(get("/api/subscriptions/{id}/qrcode", testSubscriptionId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String qrCodeData = objectMapper.readTree(qrCodeResponse).get("qrCodeData").asText();

        // Valider le QR code
        mockMvc.perform(post("/api/subscriptions/validate-qrcode")
                        .param("qrCodeData", qrCodeData))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @Order(9)
    @DisplayName("✅ TEST 9: Update Subscription - PUT /api/subscriptions/{id}")
    void test9_UpdateSubscription() throws Exception {
        UpdateSubscriptionRequest request = UpdateSubscriptionRequest.builder()
                .autoRenewEnabled(false)
                .cardToken("tok_visa_updated")
                .cardExpMonth(11)
                .cardExpYear(2026)
                .build();

        mockMvc.perform(put("/api/subscriptions/{id}", testSubscriptionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptionId").value(testSubscriptionId));
    }

    @Test
    @Order(10)
    @DisplayName("✅ TEST 10: Pause Subscription - PUT /api/subscriptions/{id}/pause")
    void test10_PauseSubscription() throws Exception {
        // S'assurer que la subscription est ACTIVE d'abord
        // (peut nécessiter un retry payment si elle est PENDING)
        
        mockMvc.perform(put("/api/subscriptions/{id}/pause", testSubscriptionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptionId").value(testSubscriptionId.toString()))
                .andExpect(jsonPath("$.status").value(SubscriptionStatus.PAUSED.name()));
    }

    @Test
    @Order(11)
    @DisplayName("✅ TEST 11: Resume Subscription - PUT /api/subscriptions/{id}/resume")
    void test11_ResumeSubscription() throws Exception {
        mockMvc.perform(put("/api/subscriptions/{id}/resume", testSubscriptionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptionId").value(testSubscriptionId.toString()))
                .andExpect(jsonPath("$.status").value(SubscriptionStatus.ACTIVE.name()));
    }

    @Test
    @Order(12)
    @DisplayName("✅ TEST 12: Retry Payment - POST /api/subscriptions/{id}/retry-payment")
    void test12_RetryPayment() throws Exception {
        // Créer une subscription PENDING pour tester le retry
        CreateSubscriptionRequest createRequest = CreateSubscriptionRequest.builder()
                .userId(200)
                .planId(testPlanId)
                .cardToken("tok_visa_test")
                .cardExpMonth(12)
                .cardExpYear(2025)
                .paymentMethod(PaymentMethod.CARD)
                .autoRenewEnabled(true)
                .build();

        String createResponse = mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer pendingSubscriptionId = objectMapper.readTree(createResponse).get("subscriptionId").asInt();

        // Tester le retry payment
        ProcessPaymentRequest paymentRequest = ProcessPaymentRequest.builder()
                .subscriptionId(pendingSubscriptionId)
                .amount(new BigDecimal("200.00"))
                .currency("MAD")
                .paymentMethod(PaymentMethod.CARD)
                .cardToken("tok_visa_test")
                .idempotencyKey(java.util.UUID.randomUUID().toString())
                .build();

        mockMvc.perform(post("/api/subscriptions/{id}/retry-payment", pendingSubscriptionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptionId").value(pendingSubscriptionId));
    }

    @Test
    @Order(13)
    @DisplayName("✅ TEST 13: Renew Subscription - PUT /api/subscriptions/{id}/renew")
    void test13_RenewSubscription() throws Exception {
        RenewSubscriptionRequest request = RenewSubscriptionRequest.builder()
                .subscriptionId(testSubscriptionId)
                .useStoredPaymentMethod(true)
                .build();

        mockMvc.perform(put("/api/subscriptions/{id}/renew", testSubscriptionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptionId").value(testSubscriptionId));
    }

    @Test
    @Order(14)
    @DisplayName("✅ TEST 14: Cancel Subscription - PUT /api/subscriptions/{id}/cancel")
    void test14_CancelSubscription() throws Exception {
        CancelSubscriptionRequest request = CancelSubscriptionRequest.builder()
                .subscriptionId(testSubscriptionId)
                .reason("User requested cancellation")
                .refundRequested(false)
                .build();

        mockMvc.perform(put("/api/subscriptions/{id}/cancel", testSubscriptionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptionId").value(testSubscriptionId.toString()))
                .andExpect(jsonPath("$.status").value(SubscriptionStatus.CANCELLED.name()));
    }

    @Test
    @Order(15)
    @DisplayName("✅ TEST 15: Get Payments by Subscription - GET /api/payments/subscription/{id}")
    void test15_GetPaymentsBySubscription() throws Exception {
        mockMvc.perform(get("/api/payments/subscription/{id}", testSubscriptionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(16)
    @DisplayName("✅ TEST 16: Update Plan - PUT /api/subscriptions/plans/{id}")
    void test16_UpdatePlan() throws Exception {
        CreatePlanRequest updateRequest = CreatePlanRequest.builder()
                .planCode(testPlanCode)
                .description("Updated description")
                .durationDays(30)
                .price(new BigDecimal("250.00"))
                .currency("MAD")
                .build();

        mockMvc.perform(put("/api/subscriptions/plans/{id}", testPlanId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planId").value(testPlanId))
                .andExpect(jsonPath("$.price").value(250.00));
    }

    @Test
    @Order(17)
    @DisplayName("✅ TEST 17: Deactivate Plan - DELETE /api/subscriptions/plans/{id}")
    void test17_DeactivatePlan() throws Exception {
        mockMvc.perform(delete("/api/subscriptions/plans/{id}", testPlanId))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(18)
    @DisplayName("✅ TEST 18: Get Plan by Code - GET /api/subscriptions/plans/code/{code}")
    void test18_GetPlanByCode() throws Exception {
        mockMvc.perform(get("/api/subscriptions/plans/code/{code}", testPlanCode))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planCode").value(testPlanCode));
    }

    @AfterAll
    static void tearDown() {
        System.out.println("\n🎉 ========================================");
        System.out.println("✅ TOUS LES TESTS D'ENDPOINTS TERMINÉS !");
        System.out.println("========================================\n");
    }
}

