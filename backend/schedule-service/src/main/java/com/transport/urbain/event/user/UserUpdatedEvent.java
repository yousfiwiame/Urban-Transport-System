package com.transport.urbain.event.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event representing an update to a user account.
 * <p>
 * This event is consumed from Kafka when user information is updated in the user-service.
 * It notifies the schedule-service of changes to user data so it can keep its
 * local user information synchronized.
 * <p>
 * The event is received from the 'user-updated-events' Kafka topic and includes:
 * <ul>
 *     <li>User ID that was updated</li>
 *     <li>Updated email address</li>
 *     <li>Updated first and last name</li>
 *     <li>Timestamp of the update event</li>
 * </ul>
 * <p>
 * The schedule-service should update its local user data to maintain consistency
 * across the microservices architecture.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatedEvent {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime timestamp;
}

