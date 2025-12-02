package com.transport.notification.exception;

/**
 * Exception thrown when notification sending fails.
 */
public class NotificationSendException extends RuntimeException {
    public NotificationSendException(String message) {
        super(message);
    }

    public NotificationSendException(String message, Throwable cause) {
        super(message, cause);
    }
}

