package com.transport.notification.consumer;

import com.transport.notification.model.NotificationEvent;
import com.transport.notification.model.enums.ProcessingStatus;
import com.transport.notification.repository.NotificationEventRepository;
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
 * Kafka consumer for geolocation-related events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LocationEventConsumer {

    private final NotificationEventRepository eventRepository;

    @KafkaListener(
        topics = "location-updated-events",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleLocationUpdated(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        log.info("Received location updated event - Key: {}", key);
        
        try {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventType("LOCATION_UPDATED")
                    .sourceService("geolocation-service")
                    .correlationId(key)
                    .processingStatus(ProcessingStatus.PROCESSING)
                    .build();
            notificationEvent = eventRepository.save(notificationEvent);

            // TODO: Send real-time location updates via push notifications if user is tracking a bus

            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);
        } catch (Exception e) {
            log.error("Error processing location updated event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
        topics = "bus-arrived-events",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleBusArrived(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        log.info("Received bus arrived event - Key: {}", key);
        
        try {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventType("BUS_ARRIVED")
                    .sourceService("geolocation-service")
                    .correlationId(key)
                    .processingStatus(ProcessingStatus.PROCESSING)
                    .build();
            notificationEvent = eventRepository.save(notificationEvent);

            // TODO: Notify users waiting at the stop that their bus has arrived

            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);
        } catch (Exception e) {
            log.error("Error processing bus arrived event: {}", e.getMessage(), e);
            throw e;
        }
    }
}

