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
 * Kafka consumer for ticket-related events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TicketEventConsumer {

    private final NotificationService notificationService;
    private final NotificationEventRepository eventRepository;

    @KafkaListener(
        topics = "ticket-purchased-events",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTicketPurchased(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        log.info("Received ticket purchased event - Key: {}", key);
        
        try {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventType("TICKET_PURCHASED")
                    .sourceService("ticket-service")
                    .correlationId(key)
                    .processingStatus(ProcessingStatus.PROCESSING)
                    .build();
            notificationEvent = eventRepository.save(notificationEvent);

            Integer userId = extractInteger(event, "userId");
            Integer ticketId = extractInteger(event, "ticketId");

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .userId(userId)
                    .title("Ticket Purchase Confirmation")
                    .messageBody(String.format("Your ticket #%d has been purchased successfully!", ticketId))
                    .channelType(ChannelType.EMAIL)
                    .templateCode("ticket-confirmation")
                    .correlationId(key)
                    .build();

            notificationService.sendNotification(request);

            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);
        } catch (Exception e) {
            log.error("Error processing ticket purchased event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
        topics = "ticket-cancelled-events",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTicketCancelled(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Received ticket cancelled event - Key: {}", key);

        try {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventType("TICKET_CANCELLED")
                    .sourceService("ticket-service")
                    .correlationId(key)
                    .processingStatus(ProcessingStatus.PROCESSING)
                    .build();
            notificationEvent = eventRepository.save(notificationEvent);

            Integer userId = extractInteger(event, "userId");
            Integer ticketId = extractInteger(event, "ticketId");
            String cancellationReason = (String) event.get("cancellationReason");

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .userId(userId)
                    .title("Ticket Cancelled")
                    .messageBody(String.format("Your ticket #%d has been cancelled. Reason: %s", ticketId, cancellationReason))
                    .channelType(ChannelType.EMAIL)
                    .templateCode("ticket-cancellation")
                    .correlationId(key)
                    .build();

            notificationService.sendNotification(request);

            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);
        } catch (Exception e) {
            log.error("Error processing ticket cancelled event: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
        topics = "ticket-validated-events",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTicketValidated(
            @Payload Map<String, Object> event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        log.info("Received ticket validated event - Key: {}", key);

        try {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .eventType("TICKET_VALIDATED")
                    .sourceService("ticket-service")
                    .correlationId(key)
                    .processingStatus(ProcessingStatus.PROCESSING)
                    .build();
            notificationEvent = eventRepository.save(notificationEvent);

            Integer userId = extractInteger(event, "userId");
            Integer ticketId = extractInteger(event, "ticketId");

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .userId(userId)
                    .title("Ticket Validated")
                    .messageBody(String.format("Your ticket #%d has been successfully validated!", ticketId))
                    .channelType(ChannelType.PUSH)
                    .templateCode("ticket-validation")
                    .correlationId(key)
                    .build();

            notificationService.sendNotification(request);

            notificationEvent.setProcessingStatus(ProcessingStatus.PROCESSED);
            notificationEvent.setProcessedAt(OffsetDateTime.now());
            eventRepository.save(notificationEvent);
        } catch (Exception e) {
            log.error("Error processing ticket validated event: {}", e.getMessage(), e);
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

