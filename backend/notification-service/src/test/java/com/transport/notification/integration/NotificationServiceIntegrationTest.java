package com.transport.notification.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.notification.dto.request.SendNotificationRequest;
import com.transport.notification.dto.response.NotificationResponse;
import com.transport.notification.model.Notification;
import com.transport.notification.model.enums.ChannelType;
import com.transport.notification.model.enums.NotificationStatus;
import com.transport.notification.repository.NotificationRepository;
import com.transport.notification.config.TestConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Notification Service.
 * Tests: Send Notifications -> Get Notifications -> Mark as Read -> Get Unread Count
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(TestConfig.class)
class NotificationServiceIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.config.enabled", () -> "false");
        // Disable Kafka for tests
        registry.add("spring.kafka.bootstrap-servers", () -> "");

        // Disable actual email/SMS sending in tests
        registry.add("mail.enabled", () -> "false");
        registry.add("sms.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationRepository notificationRepository;

    private static Integer notificationId;
    private static Integer userId = 1;

    @BeforeEach
    void setUp() {
        if (notificationId == null) {
            notificationRepository.deleteAll();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should send notification successfully")
    void testSendNotification() throws Exception {
        // Given
        SendNotificationRequest request = SendNotificationRequest.builder()
                .userId(userId)
                .title("Test Notification")
                .messageBody("This is a test notification message")
                .channelType(ChannelType.EMAIL)
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recipientId").value(userId))
                .andExpect(jsonPath("$.title").value("Test Notification"))
                .andExpect(jsonPath("$.message").value("This is a test notification message"))
                .andExpect(jsonPath("$.channel").value("EMAIL"))
                .andExpect(jsonPath("$.status").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        NotificationResponse response = objectMapper.readValue(responseBody, NotificationResponse.class);
        notificationId = response.getNotificationId();

        // Verify in database
        assertThat(notificationRepository.findById(notificationId)).isPresent();
    }

    @Test
    @Order(2)
    @DisplayName("Should get user notifications")
    void testGetUserNotifications() throws Exception {
        mockMvc.perform(get("/api/notifications/users/" + userId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].recipientId").value(userId))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @Order(3)
    @DisplayName("Should get user notifications by status")
    void testGetUserNotificationsByStatus() throws Exception {
        mockMvc.perform(get("/api/notifications/users/" + userId + "/status/PENDING")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(4)
    @DisplayName("Should get unread count")
    void testGetUnreadCount() throws Exception {
        mockMvc.perform(get("/api/notifications/users/" + userId + "/unread/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    @Order(5)
    @DisplayName("Should mark notification as read")
    void testMarkAsRead() throws Exception {
        assertThat(notificationId).isNotNull();

        mockMvc.perform(put("/api/notifications/" + notificationId + "/read")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notificationId))
                .andExpect(jsonPath("$.status").value("READ"))
                .andExpect(jsonPath("$.readAt").exists());

        // Verify in database
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.READ);
        assertThat(notification.getReadAt()).isNotNull();
    }

    @Test
    @Order(6)
    @DisplayName("Should send SMS notification")
    void testSendSmsNotification() throws Exception {
        SendNotificationRequest request = SendNotificationRequest.builder()
                .userId(userId)
                .title("SMS Notification")
                .messageBody("This is an SMS notification")
                .channelType(ChannelType.SMS)
                .build();

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.channel").value("SMS"));
    }

    @Test
    @Order(7)
    @DisplayName("Should send PUSH notification")
    void testSendPushNotification() throws Exception {
        SendNotificationRequest request = SendNotificationRequest.builder()
                .userId(userId)
                .title("Push Notification")
                .messageBody("This is a push notification")
                .channelType(ChannelType.PUSH)
                .build();

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.channel").value("PUSH"));
    }

    @Test
    @Order(8)
    @DisplayName("Should send WEBHOOK notification")
    void testSendWebhookNotification() throws Exception {
        SendNotificationRequest request = SendNotificationRequest.builder()
                .userId(userId)
                .title("Webhook Notification")
                .messageBody("This is a webhook notification")
                .channelType(ChannelType.WEBHOOK)
                .build();

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.channel").value("WEBHOOK"));
    }

    @Test
    @Order(9)
    @DisplayName("Should get all notifications for user with multiple channels")
    void testGetAllNotifications() throws Exception {
        // We sent 4 notifications total (EMAIL, SMS, PUSH, WEBHOOK)
        mockMvc.perform(get("/api/notifications/users/" + userId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(4));
    }

    @Test
    @Order(10)
    @DisplayName("Should create notification for another user")
    void testSendNotificationToAnotherUser() throws Exception {
        Integer anotherUserId = 2;

        SendNotificationRequest request = SendNotificationRequest.builder()
                .userId(anotherUserId)
                .title("Welcome Notification")
                .messageBody("Welcome to our transport system!")
                .channelType(ChannelType.EMAIL)
                .build();

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recipientId").value(anotherUserId));

        // Verify the new user has 1 notification
        mockMvc.perform(get("/api/notifications/users/" + anotherUserId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @Order(11)
    @DisplayName("Should fail to send notification with invalid data")
    void testSendNotificationWithInvalidData() throws Exception {
        // Missing required fields
        SendNotificationRequest invalidRequest = SendNotificationRequest.builder()
                .title("") // Empty title
                .messageBody("")
                .build();

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(12)
    @DisplayName("Should fail to mark non-existent notification as read")
    void testMarkNonExistentNotificationAsRead() throws Exception {
        Integer nonExistentId = 99999;

        mockMvc.perform(put("/api/notifications/" + nonExistentId + "/read")
                        .param("userId", userId.toString()))
                .andExpect(status().isNotFound());
    }

    @AfterAll
    static void tearDown() {
        postgres.stop();
    }
}
