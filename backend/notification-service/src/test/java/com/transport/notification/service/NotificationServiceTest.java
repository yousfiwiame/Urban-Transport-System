package com.transport.notification.service;

import com.transport.notification.dto.mapper.NotificationMapper;
import com.transport.notification.dto.request.SendNotificationRequest;
import com.transport.notification.dto.response.NotificationResponse;
import com.transport.notification.model.*;
import com.transport.notification.model.enums.ChannelStatus;
import com.transport.notification.model.enums.ChannelType;
import com.transport.notification.model.enums.NotificationStatus;
import com.transport.notification.repository.*;
import com.transport.notification.service.impl.NotificationServiceImpl;
import com.transport.notification.util.TemplateProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Notification Service Unit Tests")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationTemplateRepository templateRepository;

    @Mock
    private NotificationPreferenceRepository preferenceRepository;

    @Mock
    private NotificationEventRepository eventRepository;

    @Mock
    private NotificationChannelRepository channelRepository;

    @Mock
    private NotificationLogRepository logRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private EmailService emailService;

    @Mock
    private SmsService smsService;

    @Mock
    private PushNotificationService pushService;

    @Mock
    private TemplateProcessor templateProcessor;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private SendNotificationRequest testRequest;
    private Notification testNotification;
    private UserNotificationPreference testPreference;
    private NotificationTemplate testTemplate;

    @BeforeEach
    void setUp() {
        testRequest = SendNotificationRequest.builder()
                .userId(1)
                .title("Test Notification")
                .messageBody("Test message body")
                .channelType(ChannelType.EMAIL)
                .build();

        testNotification = Notification.builder()
                .notificationId(1)
                .userId(1)
                .title("Test Notification")
                .messageBody("Test message body")
                .status(NotificationStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .build();

        testPreference = UserNotificationPreference.builder()
                .preferenceId(1)
                .userId(1)
                .emailEnabled(true)
                .emailAddress("test@example.com")
                .smsEnabled(false)
                .pushEnabled(false)
                .build();

        testTemplate = NotificationTemplate.builder()
                .templateId(1)
                .templateCode("test-template")
                .channelType(ChannelType.EMAIL)
                .subject("Test Subject")
                .bodyTemplate("Hello {{name}}")
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Should send notification successfully")
    void testSendNotification_Success() {
        // Given
        when(preferenceRepository.findByUserId(1))
                .thenReturn(Optional.of(testPreference));
        when(notificationMapper.toEntity(any(SendNotificationRequest.class)))
                .thenReturn(testNotification);
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(channelRepository.findByNotification_NotificationId(anyInt()))
                .thenReturn(List.of());
        when(notificationMapper.toResponse(any(Notification.class)))
                .thenReturn(NotificationResponse.builder()
                        .notificationId(1)
                        .userId(1)
                        .title("Test Notification")
                        .status(NotificationStatus.PENDING)
                        .build());

        // When
        NotificationResponse response = notificationService.sendNotification(testRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getNotificationId()).isEqualTo(1);
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(logRepository, atLeastOnce()).save(any(NotificationLog.class));
    }

    @Test
    @DisplayName("Should create default preferences if user has none")
    void testSendNotification_CreatesDefaultPreferences() {
        // Given
        when(preferenceRepository.findByUserId(1))
                .thenReturn(Optional.empty());
        when(preferenceRepository.save(any(UserNotificationPreference.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationMapper.toEntity(any(SendNotificationRequest.class)))
                .thenReturn(testNotification);
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(channelRepository.findByNotification_NotificationId(anyInt()))
                .thenReturn(List.of());
        when(notificationMapper.toResponse(any(Notification.class)))
                .thenReturn(NotificationResponse.builder().notificationId(1).build());

        // When
        notificationService.sendNotification(testRequest);

        // Then
        ArgumentCaptor<UserNotificationPreference> preferenceCaptor = 
                ArgumentCaptor.forClass(UserNotificationPreference.class);
        verify(preferenceRepository).save(preferenceCaptor.capture());
        assertThat(preferenceCaptor.getValue().getUserId()).isEqualTo(1);
        assertThat(preferenceCaptor.getValue().getEmailEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should process template if template code provided")
    void testSendNotification_WithTemplate() {
        // Given
        testRequest.setTemplateCode("test-template");
        testRequest.setTemplateVariables(Map.of("name", "John"));

        when(preferenceRepository.findByUserId(1))
                .thenReturn(Optional.of(testPreference));
        when(templateRepository.findByTemplateCode("test-template"))
                .thenReturn(Optional.of(testTemplate));
        when(templateProcessor.processTemplate(anyString(), anyMap()))
                .thenReturn("Hello John");
        when(notificationMapper.toEntity(any(SendNotificationRequest.class)))
                .thenReturn(testNotification);
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(channelRepository.findByNotification_NotificationId(anyInt()))
                .thenReturn(List.of());
        when(notificationMapper.toResponse(any(Notification.class)))
                .thenReturn(NotificationResponse.builder().notificationId(1).build());

        // When
        notificationService.sendNotification(testRequest);

        // Then
        verify(templateRepository).findByTemplateCode("test-template");
        verify(templateProcessor).processTemplate(anyString(), anyMap());
    }

    @Test
    @DisplayName("Should get user notifications with pagination")
    void testGetUserNotifications_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Notification> notificationPage = new PageImpl<>(List.of(testNotification), pageable, 1);
        
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1, pageable))
                .thenReturn(notificationPage);
        when(notificationMapper.toResponse(any(Notification.class)))
                .thenReturn(NotificationResponse.builder()
                        .notificationId(1)
                        .userId(1)
                        .build());

        // When
        Page<NotificationResponse> response = notificationService.getUserNotifications(1, pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(1, pageable);
    }

    @Test
    @DisplayName("Should mark notification as read")
    void testMarkAsRead_Success() {
        // Given
        when(notificationRepository.findById(1))
                .thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationMapper.toResponse(any(Notification.class)))
                .thenReturn(NotificationResponse.builder()
                        .notificationId(1)
                        .status(NotificationStatus.READ)
                        .build());

        // When
        NotificationResponse response = notificationService.markAsRead(1, 1);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(NotificationStatus.READ);
        verify(notificationRepository).save(any(Notification.class));
        verify(logRepository).save(any(NotificationLog.class));
    }

    @Test
    @DisplayName("Should throw exception when marking notification as read for different user")
    void testMarkAsRead_Unauthorized() {
        // Given
        when(notificationRepository.findById(1))
                .thenReturn(Optional.of(testNotification));

        // When/Then
        assertThatThrownBy(() -> notificationService.markAsRead(1, 999))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    @DisplayName("Should get unread count for user")
    void testGetUnreadCount_Success() {
        // Given
        when(notificationRepository.countUnreadByUserId(1))
                .thenReturn(5L);

        // When
        Long count = notificationService.getUnreadCount(1);

        // Then
        assertThat(count).isEqualTo(5L);
        verify(notificationRepository).countUnreadByUserId(1);
    }

    @Test
    @DisplayName("Should send email notification successfully")
    void testSendNotification_EmailChannel() {
        // Given
        testRequest.setChannelType(ChannelType.EMAIL);
        NotificationChannel emailChannel = NotificationChannel.builder()
                .channelId(1)
                .channelType(ChannelType.EMAIL)
                .channelStatus(ChannelStatus.PENDING)
                .notification(testNotification)
                .build();

        when(preferenceRepository.findByUserId(1))
                .thenReturn(Optional.of(testPreference));
        when(notificationMapper.toEntity(any(SendNotificationRequest.class)))
                .thenReturn(testNotification);
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(channelRepository.save(any(NotificationChannel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(channelRepository.findByNotification_NotificationId(1))
                .thenReturn(List.of(emailChannel));
        when(emailService.sendEmail(anyString(), anyString(), anyString()))
                .thenReturn(true);
        when(notificationMapper.toResponse(any(Notification.class)))
                .thenReturn(NotificationResponse.builder().notificationId(1).build());

        // When
        notificationService.sendNotification(testRequest);
        notificationService.processPendingNotifications();

        // Then
        verify(emailService).sendEmail(eq("test@example.com"), anyString(), anyString());
    }
}

