package com.transport.urbain.event.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event representing the deletion of a user account.
 * <p>
 * This event is consumed from Kafka when a user account is permanently deleted
 * from the user-service. It notifies the schedule-service that the user no longer
 * exists and it should clean up any user-specific data.
 * <p>
 * The event is received from the 'user-deleted-events' Kafka topic and includes:
 * <ul>
 *     <li>User ID that was deleted</li>
 *     <li>Email address of the deleted user</li>
 *     <li>Timestamp of the deletion event</li>
 * </ul>
 * <p>
 * The schedule-service should:
 * <ul>
 *     <li>Delete or anonymize user-specific schedule preferences</li>
 *     <li>Remove favorite routes associated with the user</li>
 *     <li>Cancel any active schedule notifications</li>
 *     <li>Archive or remove historical data as per data retention policies</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletedEvent {
    private Long userId;
    private String email;
    private LocalDateTime timestamp;
}

