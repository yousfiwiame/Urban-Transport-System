package com.transport.notification.model.enums;

/**
 * Enumeration representing the status of a notification.
 * 
 * <p>Status flow:
 * <ul>
 *   <li>PENDING - Notification created but not yet sent</li>
 *   <li>SENDING - Notification is currently being sent</li>
 *   <li>SENT - Notification has been sent successfully</li>
 *   <li>DELIVERED - Notification has been delivered to the recipient</li>
 *   <li>FAILED - Notification failed to send</li>
 *   <li>READ - Notification has been read by the user</li>
 * </ul>
 */
public enum NotificationStatus {
    PENDING,
    SENDING,
    SENT,
    DELIVERED,
    FAILED,
    READ
}

