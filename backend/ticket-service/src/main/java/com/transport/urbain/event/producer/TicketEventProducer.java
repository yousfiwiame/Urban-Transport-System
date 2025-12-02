package com.transport.urbain.event.producer;

import com.transport.urbain.event.TicketCancelledEvent;
import com.transport.urbain.event.TicketPurchasedEvent;
import com.transport.urbain.event.TicketValidatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Event producer for publishing ticket-related events to Kafka.
 *
 * <p>This component handles publishing ticket lifecycle events to Kafka topics
 * for asynchronous communication with other microservices in the system.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TicketEventProducer {

    private static final String TICKET_PURCHASED_TOPIC = "ticket-purchased-events";
    private static final String TICKET_CANCELLED_TOPIC = "ticket-cancelled-events";
    private static final String TICKET_VALIDATED_TOPIC = "ticket-validated-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes a ticket purchased event to Kafka.
     *
     * @param event the ticket purchased event to publish
     */
    public void publishTicketPurchased(TicketPurchasedEvent event) {
        log.info("Publishing ticket purchased event: {}", event);
        kafkaTemplate.send(TICKET_PURCHASED_TOPIC, event.getTicketId().toString(), event);
    }

    /**
     * Publishes a ticket cancelled event to Kafka.
     *
     * @param event the ticket cancelled event to publish
     */
    public void publishTicketCancelled(TicketCancelledEvent event) {
        log.info("Publishing ticket cancelled event: {}", event);
        kafkaTemplate.send(TICKET_CANCELLED_TOPIC, event.getTicketId().toString(), event);
    }

    /**
     * Publishes a ticket validated event to Kafka.
     *
     * @param event the ticket validated event to publish
     */
    public void publishTicketValidated(TicketValidatedEvent event) {
        log.info("Publishing ticket validated event: {}", event);
        kafkaTemplate.send(TICKET_VALIDATED_TOPIC, event.getTicketId().toString(), event);
    }
}
