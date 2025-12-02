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
 * Kafka consumer for geolocation-related events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LocationEventConsumer {

    private final NotificationService notificationService;
    private final NotificationEventRepository eventRepository;

    @KafkaListener(
        topics = "bus-location-updated-events",
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

            String busId = (String) event.get("busId");
            String currentRoute = (String) event.get("currentRoute");
            String nextStop = (String) event.get("nextStop");

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .userId(null) // Broadcast to users tracking this bus
                    .title("Bus Location Update")
                    .messageBody(String.format("Bus %s on route %s is approaching %s", busId, currentRoute, nextStop))
                    .channelType(ChannelType.PUSH)
                    .templateCode("location-update")
                    .correlationId(key)
                    .build();

            notificationService.sendNotification(request);

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

            String busId = (String) event.get("busId");
            String stopName = (String) event.get("stopName");
            String routeName = (String) event.get("routeName");

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .userId(null) // Broadcast to users at this stop
                    .title("Bus Arrival")
                    .messageBody(String.format("Bus %s on route %s has arrived at %s", busId, routeName, stopName))
                    .channelType(ChannelType.PUSH)
                    .templateCode("bus-arrival")
                    .correlationId(key)
                    .build();

            notificationService.sendNotification(request);

            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);
        } catch (Exception e) {
            log.error("Error processing bus arrived event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
        topics = "bus-departed-events",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleBusDeparted(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Received bus departed event - Key: {}", key);

        try {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventType("BUS_DEPARTED")
                    .sourceService("geolocation-service")
                    .correlationId(key)
                    .processingStatus(ProcessingStatus.PROCESSING)
                    .build();
            notificationEvent = eventRepository.save(notificationEvent);

            String busId = (String) event.get("busId");
            String stopName = (String) event.get("stopName");
            String nextStopName = (String) event.get("nextStopName");
            String routeName = (String) event.get("routeName");

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .userId(null) // Broadcast to users tracking this route
                    .title("Bus Departure")
                    .messageBody(String.format("Bus %s on route %s has departed from %s, heading to %s",
                        busId, routeName, stopName, nextStopName))
                    .channelType(ChannelType.PUSH)
                    .templateCode("bus-departure")
                    .correlationId(key)
                    .build();

            notificationService.sendNotification(request);

            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);
        } catch (Exception e) {
            log.error("Error processing bus departed event: {}", e.getMessage(), e);
            throw e;
        }
    }
}

