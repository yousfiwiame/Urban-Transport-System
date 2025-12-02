package com.transport.notification.model.enums;

/**
 * Enumeration representing the processing status of a notification event.
 * 
 * <p>Status flow:
 * <ul>
 *   <li>RECEIVED - Event received from Kafka but not yet processed</li>
 *   <li>PROCESSING - Event is currently being processed</li>
 *   <li>PROCESSED - Event has been successfully processed</li>
 *   <li>FAILED - Event processing failed</li>
 * </ul>
 */
public enum ProcessingStatus {
    RECEIVED,
    PROCESSING,
    PROCESSED,
    FAILED
}

