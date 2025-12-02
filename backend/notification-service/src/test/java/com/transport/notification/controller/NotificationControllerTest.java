package com.transport.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.notification.dto.request.SendNotificationRequest;
import com.transport.notification.dto.response.NotificationResponse;
import com.transport.notification.model.enums.ChannelType;
import com.transport.notification.model.enums.NotificationStatus;
import com.transport.notification.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = NotificationController.class,
    excludeAutoConfiguration = {
        JpaRepositoriesAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
    }
)
@DisplayName("Notification Controller Tests")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should send notification successfully")
    void testSendNotification_Success() throws Exception {
        // Given
        SendNotificationRequest request = SendNotificationRequest.builder()
                .userId(1)
                .title("Test Notification")
                .messageBody("Test message")
                .channelType(ChannelType.EMAIL)
                .build();

        NotificationResponse response = NotificationResponse.builder()
                .notificationId(1)
                .userId(1)
                .title("Test Notification")
                .status(NotificationStatus.PENDING)
                .build();

        when(notificationService.sendNotification(any(SendNotificationRequest.class)))
                .thenReturn(response);

        // When/Then
        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Notification"));
    }

    @Test
    @DisplayName("Should get user notifications")
    void testGetUserNotifications_Success() throws Exception {
        // Given
        NotificationResponse response = NotificationResponse.builder()
                .notificationId(1)
                .userId(1)
                .title("Test")
                .build();

        Page<NotificationResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);

        when(notificationService.getUserNotifications(eq(1), any()))
                .thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/notifications/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should mark notification as read")
    void testMarkAsRead_Success() throws Exception {
        // Given
        NotificationResponse response = NotificationResponse.builder()
                .notificationId(1)
                .userId(1)
                .status(NotificationStatus.READ)
                .build();

        when(notificationService.markAsRead(1, 1))
                .thenReturn(response);

        // When/Then
        mockMvc.perform(put("/api/notifications/1/read")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READ"));
    }

    @Test
    @DisplayName("Should get unread count")
    void testGetUnreadCount_Success() throws Exception {
        // Given
        when(notificationService.getUnreadCount(1))
                .thenReturn(5L);

        // When/Then
        mockMvc.perform(get("/api/notifications/users/1/unread/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    @Test
    @DisplayName("Should validate request body")
    void testSendNotification_ValidationError() throws Exception {
        // Given
        SendNotificationRequest invalidRequest = SendNotificationRequest.builder()
                .userId(null) // Invalid: userId is required
                .title("") // Invalid: title cannot be blank
                .build();

        // When/Then
        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}

