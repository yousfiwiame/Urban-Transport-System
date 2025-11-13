package com.transport.ticket.exception;

/**
 * Exception levée quand un ticket est invalide
 */
public class InvalidTicketException extends RuntimeException {

    public InvalidTicketException(String message) {
        super(message);
    }

    public InvalidTicketException(String message, Throwable cause) {
        super(message, cause);
    }
}