package com.transport.notification.exception;

/**
 * Exception thrown when a notification template is not found.
 */
public class TemplateNotFoundException extends RuntimeException {
    public TemplateNotFoundException(String message) {
        super(message);
    }
}

