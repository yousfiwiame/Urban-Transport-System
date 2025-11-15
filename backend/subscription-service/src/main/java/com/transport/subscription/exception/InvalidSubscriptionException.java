package com.transport.subscription.exception;

public class InvalidSubscriptionException extends RuntimeException {
    public InvalidSubscriptionException(String message) {
        super(message);
    }

    public InvalidSubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}

