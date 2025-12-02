package com.transport.notification.model.enums;

/**
 * Enumeration representing the status of a notification channel delivery attempt.
 * 
 * <p>Status values:
 * <ul>
 *   <li>PENDING - Channel delivery not yet attempted</li>
 *   <li>SENDING - Channel delivery in progress</li>
 *   <li>SUCCESS - Channel delivery successful</li>
 *   <li>FAILED - Channel delivery failed</li>
 *   <li>RETRYING - Channel delivery failed and will be retried</li>
 * </ul>
 */
public enum ChannelStatus {
    PENDING,
    SENDING,
    SUCCESS,
    FAILED,
    RETRYING
}

