package com.transport.ticket.exception;

/**
 * Exception levée quand un ticket n'est pas trouvé
 */
public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(String message) {
        super(message);
    }

    public TicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}