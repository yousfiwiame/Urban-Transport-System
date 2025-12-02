package com.transport.notification.exception;

/**
 * Exception thrown when notification recipient is invalid.
 */
public class InvalidRecipientException extends RuntimeException {
    public InvalidRecipientException(String message) {
        super(message);
    }
}

