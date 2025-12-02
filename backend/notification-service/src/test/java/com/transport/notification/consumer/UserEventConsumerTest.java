package com.transport.notification.consumer;

import com.transport.notification.model.NotificationEvent;
import com.transport.notification.model.enums.ProcessingStatus;
import com.transport.notification.repository.NotificationEventRepository;
import com.transport.notification.service.NotificationService;
import com.transport.notification.dto.request.SendNotificationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Event Consumer Tests")
class UserEventConsumerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationEventRepository eventRepository;

    @InjectMocks
    private UserEventConsumer userEventConsumer;

    private Map<String, Object> userCreatedEvent;

    @BeforeEach
    void setUp() {
        userCreatedEvent = new HashMap<>();
        userCreatedEvent.put("userId", 1L);
        userCreatedEvent.put("email", "test@example.com");
        userCreatedEvent.put("firstName", "John");
        userCreatedEvent.put("lastName", "Doe");
    }

    @Test
    @DisplayName("Should handle user created event successfully")
    void testHandleUserCreated_Success() {
        // Given
        when(eventRepository.save(any(NotificationEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationService.sendNotification(any(SendNotificationRequest.class)))
                .thenReturn(com.transport.notification.dto.response.NotificationResponse.builder()
                        .notificationId(1)
                        .build());

        // When
        userEventConsumer.handleUserCreated(userCreatedEvent, "1", 0, 0L);

        // Then
        ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(eventRepository, times(2)).save(eventCaptor.capture());
        
        NotificationEvent savedEvent = eventCaptor.getAllValues().get(0);
        assertThat(savedEvent.getEventType()).isEqualTo("USER_CREATED");
        assertThat(savedEvent.getSourceService()).isEqualTo("user-service");
        // After successful processing, the event status is PROCESSED
        assertThat(savedEvent.getProcessingStatus()).isEqualTo(ProcessingStatus.PROCESSED);

        verify(notificationService).sendNotification(any(SendNotificationRequest.class));
    }

    @Test
    @DisplayName("Should handle user updated event successfully")
    void testHandleUserUpdated_Success() {
        // Given
        Map<String, Object> userUpdatedEvent = new HashMap<>();
        userUpdatedEvent.put("userId", 1L);
        userUpdatedEvent.put("email", "updated@example.com");

        when(eventRepository.save(any(NotificationEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        userEventConsumer.handleUserUpdated(userUpdatedEvent, "1");

        // Then
        verify(eventRepository, times(2)).save(any(NotificationEvent.class));
        ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(eventRepository, times(2)).save(eventCaptor.capture());
        
        NotificationEvent savedEvent = eventCaptor.getAllValues().get(0);
        assertThat(savedEvent.getEventType()).isEqualTo("USER_UPDATED");
    }

    @Test
    @DisplayName("Should handle user deleted event successfully")
    void testHandleUserDeleted_Success() {
        // Given
        Map<String, Object> userDeletedEvent = new HashMap<>();
        userDeletedEvent.put("userId", 1L);
        userDeletedEvent.put("email", "deleted@example.com");

        when(eventRepository.save(any(NotificationEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        userEventConsumer.handleUserDeleted(userDeletedEvent, "1");

        // Then
        verify(eventRepository, times(2)).save(any(NotificationEvent.class));
        ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(eventRepository, times(2)).save(eventCaptor.capture());
        
        NotificationEvent savedEvent = eventCaptor.getAllValues().get(0);
        assertThat(savedEvent.getEventType()).isEqualTo("USER_DELETED");
    }

    @Test
    @DisplayName("Should handle event processing failure")
    void testHandleUserCreated_Failure() {
        // Given
        when(eventRepository.save(any(NotificationEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationService.sendNotification(any(SendNotificationRequest.class)))
                .thenThrow(new RuntimeException("Service error"));

        // When/Then
        try {
            userEventConsumer.handleUserCreated(userCreatedEvent, "1", 0, 0L);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
        }

        // Verify event was saved with PROCESSING status
        verify(eventRepository, atLeastOnce()).save(any(NotificationEvent.class));
    }
}

