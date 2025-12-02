package com.transport.notification.consumer;

import com.transport.notification.model.NotificationEvent;
import com.transport.notification.model.enums.ProcessingStatus;
import com.transport.notification.repository.NotificationEventRepository;
import com.transport.notification.service.NotificationService;
import com.transport.notification.dto.request.SendNotificationRequest;
import com.transport.notification.model.enums.ChannelType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Kafka consumer for user-related events.
 * 
 * <p>Listens to events from user-service and triggers appropriate notifications.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final NotificationService notificationService;
    private final NotificationEventRepository eventRepository;

    @KafkaListener(
        topics = "user-created-events",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserCreated(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received user created event - Key: {}, Partition: {}, Offset: {}", key, partition, offset);
        
        try {
            // Save event
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventType("USER_CREATED")
                    .sourceService("user-service")
                    .correlationId(key)
                    .processingStatus(ProcessingStatus.PROCESSING)
                    .build();
            notificationEvent = eventRepository.save(notificationEvent);

            // Extract user data
            Integer userId = extractInteger(event, "userId");
            String email = extractString(event, "email");
            String firstName = extractString(event, "firstName");
            String lastName = extractString(event, "lastName");

            // Send welcome notification
            SendNotificationRequest request = SendNotificationRequest.builder()
                    .userId(userId)
                    .title("Welcome to Urban Transport!")
                    .messageBody(String.format("Hello %s %s, welcome to our transportation system!", firstName, lastName))
                    .channelType(ChannelType.EMAIL)
                    .templateCode("welcome-email")
                    .correlationId(key)
                    .build();

            notificationService.sendNotification(request);

            // Mark event as processed
            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);

            log.info("Successfully processed user created event for user: {}", userId);
        } catch (Exception e) {
            log.error("Error processing user created event: {}", e.getMessage(), e);
            throw e; // Re-throw to trigger Kafka retry mechanism
        }
    }

    @KafkaListener(
        topics = "user-updated-events",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserUpdated(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        log.info("Received user updated event - Key: {}", key);
        
        try {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventType("USER_UPDATED")
                    .sourceService("user-service")
                    .correlationId(key)
                    .processingStatus(ProcessingStatus.PROCESSING)
                    .build();
            notificationEvent = eventRepository.save(notificationEvent);

            // TODO: Send notification if needed (e.g., profile update confirmation)

            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);
        } catch (Exception e) {
            log.error("Error processing user updated event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
        topics = "user-deleted-events",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserDeleted(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        log.info("Received user deleted event - Key: {}", key);
        
        try {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventType("USER_DELETED")
                    .sourceService("user-service")
                    .correlationId(key)
                    .processingStatus(ProcessingStatus.PROCESSING)
                    .build();
            notificationEvent = eventRepository.save(notificationEvent);

            // TODO: Clean up user preferences and notification history

            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);
        } catch (Exception e) {
            log.error("Error processing user deleted event: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Integer extractInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof Number) return ((Number) value).intValue();
        return Integer.parseInt(value.toString());
    }

    private String extractString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }
}

