package com.transport.urbain.event.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event representing the creation of a new user account.
 * <p>
 * This event is consumed from Kafka when a new user successfully registers
 * in the user-service. It contains the essential user information needed by
 * the schedule-service to initialize user-specific data.
 * <p>
 * The event is received from the 'user-created-events' Kafka topic and includes:
 * <ul>
 *     <li>User ID for reference</li>
 *     <li>Email address</li>
 *     <li>User's first and last name</li>
 *     <li>Timestamp of the event</li>
 * </ul>
 * <p>
 * The schedule-service can use this event to:
 * <ul>
 *     <li>Initialize user preferences for schedules</li>
 *     <li>Set up default favorite routes</li>
 *     <li>Create user-specific schedule notifications</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime timestamp;
}

