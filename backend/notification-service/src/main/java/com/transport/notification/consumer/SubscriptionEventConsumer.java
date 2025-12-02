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
 * Kafka consumer for subscription-related events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionEventConsumer {

    private final NotificationService notificationService;
    private final NotificationEventRepository eventRepository;

    @KafkaListener(
        topics = "subscription-created",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSubscriptionCreated(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        log.info("Received subscription created event - Key: {}", key);
        
        try {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventType("SUBSCRIPTION_CREATED")
                    .sourceService("subscription-service")
                    .correlationId(key)
                    .processingStatus(ProcessingStatus.PROCESSING)
                    .build();
            notificationEvent = eventRepository.save(notificationEvent);

            Integer userId = extractInteger(event, "userId");
            Integer subscriptionId = extractInteger(event, "subscriptionId");

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .userId(userId)
                    .title("Subscription Activated")
                    .messageBody(String.format("Your subscription #%d has been successfully activated!", subscriptionId))
                    .channelType(ChannelType.EMAIL)
                    .templateCode("subscription-confirmation")
                    .correlationId(key)
                    .build();

            notificationService.sendNotification(request);

            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);
        } catch (Exception e) {
            log.error("Error processing subscription created event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
        topics = "subscription-renewed",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSubscriptionRenewed(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        log.info("Received subscription renewed event - Key: {}", key);
        
        try {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventType("SUBSCRIPTION_RENEWED")
                    .sourceService("subscription-service")
                    .correlationId(key)
                    .processingStatus(ProcessingStatus.PROCESSING)
                    .build();
            notificationEvent = eventRepository.save(notificationEvent);

            Integer userId = extractInteger(event, "userId");

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .userId(userId)
                    .title("Subscription Renewed")
                    .messageBody("Your subscription has been successfully renewed!")
                    .channelType(ChannelType.EMAIL)
                    .templateCode("subscription-renewed")
                    .correlationId(key)
                    .build();

            notificationService.sendNotification(request);

            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);
        } catch (Exception e) {
            log.error("Error processing subscription renewed event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
        topics = "subscription-cancelled",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleSubscriptionCancelled(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        log.info("Received subscription cancelled event - Key: {}", key);
        
        try {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventType("SUBSCRIPTION_CANCELLED")
                    .sourceService("subscription-service")
                    .correlationId(key)
                    .processingStatus(ProcessingStatus.PROCESSING)
                    .build();
            notificationEvent = eventRepository.save(notificationEvent);

            Integer userId = extractInteger(event, "userId");

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .userId(userId)
                    .title("Subscription Cancelled")
                    .messageBody("Your subscription has been cancelled. We're sorry to see you go!")
                    .channelType(ChannelType.EMAIL)
                    .templateCode("subscription-cancelled")
                    .correlationId(key)
                    .build();

            notificationService.sendNotification(request);

            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);
        } catch (Exception e) {
            log.error("Error processing subscription cancelled event: {}", e.getMessage(), e);
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
}

