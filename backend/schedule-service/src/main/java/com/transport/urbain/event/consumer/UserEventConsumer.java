package com.transport.urbain.event.consumer;

import com.transport.urbain.event.user.UserCreatedEvent;
import com.transport.urbain.event.user.UserDeletedEvent;
import com.transport.urbain.event.user.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Consumer for user-related events from Kafka.
 * <p>
 * This component listens to user events published by the user-service and processes
 * them to keep the schedule-service synchronized with user data changes.
 * <p>
 * Supported events:
 * <ul>
 *     <li>User Created - Received from 'user-created-events' topic</li>
 *     <li>User Updated - Received from 'user-updated-events' topic</li>
 *     <li>User Deleted - Received from 'user-deleted-events' topic</li>
 * </ul>
 * <p>
 * Each event handler logs the received event and performs necessary actions
 * to maintain data consistency across services.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    /**
     * Handles user created events from Kafka.
     * <p>
     * This method is invoked when a new user is created in the user-service.
     * It processes the event to initialize user-specific data in the schedule-service.
     *
     * @param event the user created event
     * @param key the Kafka message key (user ID)
     * @param partition the Kafka partition number
     * @param offset the message offset
     * @param acknowledgment Kafka acknowledgment for manual commit
     */
    @KafkaListener(
        topics = "user-created-events",
        groupId = "schedule-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserCreated(
            @Payload UserCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received user created event - UserId: {}, Email: {}, Partition: {}, Offset: {}", 
                event.getUserId(), event.getEmail(), partition, offset);
        
        try {
            // TODO: Initialize user-specific schedule preferences
            // TODO: Set up default favorite routes if needed
            // TODO: Create user-specific schedule notifications
            
            log.info("Successfully processed user created event for user: {}", event.getUserId());
        } catch (Exception e) {
            log.error("Error processing user created event for user: {}", event.getUserId(), e);
            // In production, you might want to implement retry logic or dead letter queue
            throw e; // Re-throw to trigger Kafka retry mechanism
        }
    }

    /**
     * Handles user updated events from Kafka.
     * <p>
     * This method is invoked when user information is updated in the user-service.
     * It processes the event to update user-specific data in the schedule-service.
     *
     * @param event the user updated event
     * @param key the Kafka message key (user ID)
     * @param partition the Kafka partition number
     * @param offset the message offset
     * @param acknowledgment Kafka acknowledgment for manual commit
     */
    @KafkaListener(
        topics = "user-updated-events",
        groupId = "schedule-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserUpdated(
            @Payload UserUpdatedEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received user updated event - UserId: {}, Email: {}, Partition: {}, Offset: {}", 
                event.getUserId(), event.getEmail(), partition, offset);
        
        try {
            // TODO: Update user-specific schedule preferences
            // TODO: Update user notifications if email changed
            // TODO: Sync user data in local cache if applicable
            
            log.info("Successfully processed user updated event for user: {}", event.getUserId());
        } catch (Exception e) {
            log.error("Error processing user updated event for user: {}", event.getUserId(), e);
            throw e; // Re-throw to trigger Kafka retry mechanism
        }
    }

    /**
     * Handles user deleted events from Kafka.
     * <p>
     * This method is invoked when a user account is deleted from the user-service.
     * It processes the event to clean up user-specific data in the schedule-service.
     *
     * @param event the user deleted event
     * @param key the Kafka message key (user ID)
     * @param partition the Kafka partition number
     * @param offset the message offset
     * @param acknowledgment Kafka acknowledgment for manual commit
     */
    @KafkaListener(
        topics = "user-deleted-events",
        groupId = "schedule-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserDeleted(
            @Payload UserDeletedEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received user deleted event - UserId: {}, Email: {}, Partition: {}, Offset: {}", 
                event.getUserId(), event.getEmail(), partition, offset);
        
        try {
            // TODO: Delete user-specific schedule preferences
            // TODO: Remove favorite routes associated with the user
            // TODO: Cancel active schedule notifications
            // TODO: Archive or anonymize historical data
            
            log.info("Successfully processed user deleted event for user: {}", event.getUserId());
        } catch (Exception e) {
            log.error("Error processing user deleted event for user: {}", event.getUserId(), e);
            throw e; // Re-throw to trigger Kafka retry mechanism
        }
    }
}

