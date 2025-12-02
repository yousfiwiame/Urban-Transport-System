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
 * Kafka consumer for schedule-related events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleEventConsumer {

    private final NotificationService notificationService;
    private final NotificationEventRepository eventRepository;

    @KafkaListener(
        topics = "schedule-updated-events",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleScheduleUpdated(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        log.info("Received schedule updated event - Key: {}", key);
        
        try {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventType("SCHEDULE_UPDATED")
                    .sourceService("schedule-service")
                    .correlationId(key)
                    .processingStatus(ProcessingStatus.PROCESSING)
                    .build();
            notificationEvent = eventRepository.save(notificationEvent);

            String routeName = (String) event.get("routeName");
            String changeDescription = (String) event.get("changeDescription");

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .userId(null) // Broadcast to all affected users
                    .title("Schedule Update")
                    .messageBody(String.format("Schedule updated for route %s: %s", routeName, changeDescription))
                    .channelType(ChannelType.PUSH)
                    .templateCode("schedule-update")
                    .correlationId(key)
                    .build();

            notificationService.sendNotification(request);

            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);
        } catch (Exception e) {
            log.error("Error processing schedule updated event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
        topics = "route-changed-events",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleRouteChanged(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        log.info("Received route changed event - Key: {}", key);
        
        try {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventType("ROUTE_CHANGED")
                    .sourceService("schedule-service")
                    .correlationId(key)
                    .processingStatus(ProcessingStatus.PROCESSING)
                    .build();
            notificationEvent = eventRepository.save(notificationEvent);

            String routeName = (String) event.get("routeName");
            String changeType = (String) event.get("changeType");

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .userId(null) // Broadcast to all affected users
                    .title("Route Change Alert")
                    .messageBody(String.format("Route %s has been %s. Please check the updated schedule.", routeName, changeType))
                    .channelType(ChannelType.PUSH)
                    .templateCode("route-change")
                    .correlationId(key)
                    .build();

            notificationService.sendNotification(request);

            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);
        } catch (Exception e) {
            log.error("Error processing route changed event: {}", e.getMessage(), e);
            throw e;
        }
    }
}

