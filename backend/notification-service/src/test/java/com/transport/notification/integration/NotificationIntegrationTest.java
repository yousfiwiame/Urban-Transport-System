package com.transport.notification.integration;

import com.transport.notification.dto.request.SendNotificationRequest;
import com.transport.notification.dto.response.NotificationResponse;
import com.transport.notification.model.Notification;
import com.transport.notification.model.UserNotificationPreference;
import com.transport.notification.model.enums.ChannelType;
import com.transport.notification.model.enums.NotificationStatus;
import com.transport.notification.repository.NotificationRepository;
import com.transport.notification.repository.NotificationPreferenceRepository;
import com.transport.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Notification Service Integration Tests")
class NotificationIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        notificationRepository.deleteAll();
        preferenceRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create and send notification end-to-end")
    void testSendNotification_EndToEnd() {
        // Given
        SendNotificationRequest request = SendNotificationRequest.builder()
                .userId(1)
                .title("Integration Test Notification")
                .messageBody("This is a test notification")
                .channelType(ChannelType.EMAIL)
                .build();

        // Create user preference
        UserNotificationPreference preference = UserNotificationPreference.builder()
                .userId(1)
                .emailEnabled(true)
                .emailAddress("test@example.com")
                .smsEnabled(false)
                .pushEnabled(false)
                .build();
        preferenceRepository.save(preference);

        // When
        NotificationResponse response = notificationService.sendNotification(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getNotificationId()).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1);
        assertThat(response.getTitle()).isEqualTo("Integration Test Notification");

        // Verify notification was saved
        Notification savedNotification = notificationRepository.findById(response.getNotificationId())
                .orElseThrow();
        assertThat(savedNotification.getStatus()).isIn(NotificationStatus.PENDING, NotificationStatus.SENT);
    }

    @Test
    @DisplayName("Should retrieve user notifications")
    void testGetUserNotifications_EndToEnd() {
        // Given
        Notification notification1 = Notification.builder()
                .userId(1)
                .title("Notification 1")
                .messageBody("Body 1")
                .status(NotificationStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .build();

        Notification notification2 = Notification.builder()
                .userId(1)
                .title("Notification 2")
                .messageBody("Body 2")
                .status(NotificationStatus.SENT)
                .createdAt(OffsetDateTime.now().plusSeconds(1))
                .build();

        notificationRepository.save(notification1);
        notificationRepository.save(notification2);

        // When
        var result = notificationService.getUserNotifications(1, 
                org.springframework.data.domain.PageRequest.of(0, 10));

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(NotificationResponse::getUserId)
                .containsOnly(1);
    }

    @Test
    @DisplayName("Should mark notification as read")
    void testMarkAsRead_EndToEnd() {
        // Given
        Notification notification = Notification.builder()
                .userId(1)
                .title("Test")
                .messageBody("Body")
                .status(NotificationStatus.SENT)
                .createdAt(OffsetDateTime.now())
                .build();
        notification = notificationRepository.save(notification);

        // When
        NotificationResponse response = notificationService.markAsRead(
                notification.getNotificationId(), 1);

        // Then
        assertThat(response.getStatus()).isEqualTo(NotificationStatus.READ);
        assertThat(response.getReadAt()).isNotNull();

        // Verify in database
        Notification updated = notificationRepository.findById(notification.getNotificationId())
                .orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(NotificationStatus.READ);
        assertThat(updated.getReadAt()).isNotNull();
    }

    @Test
    @DisplayName("Should count unread notifications")
    void testGetUnreadCount_EndToEnd() {
        // Given
        Notification read = Notification.builder()
                .userId(1)
                .title("Read")
                .messageBody("Body")
                .status(NotificationStatus.READ)
                .readAt(OffsetDateTime.now())
                .createdAt(OffsetDateTime.now())
                .build();

        Notification pending = Notification.builder()
                .userId(1)
                .title("Pending")
                .messageBody("Body")
                .status(NotificationStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .build();

        Notification sent = Notification.builder()
                .userId(1)
                .title("Sent")
                .messageBody("Body")
                .status(NotificationStatus.SENT)
                .createdAt(OffsetDateTime.now())
                .build();

        notificationRepository.save(read);
        notificationRepository.save(pending);
        notificationRepository.save(sent);

        // When
        Long count = notificationService.getUnreadCount(1);

        // Then
        assertThat(count).isEqualTo(2L); // PENDING and SENT are not READ
    }
}

