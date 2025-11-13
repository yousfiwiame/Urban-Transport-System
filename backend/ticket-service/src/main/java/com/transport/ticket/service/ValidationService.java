package com.transport.ticket.service;

import com.transport.ticket.dto.request.ValidateTicketRequest;
import com.transport.ticket.dto.response.ValidationResponse;

import java.util.List;

/**
 * Interface du service de validation
 */
public interface ValidationService {

    /**
     * Valider un ticket (scan QR code)
     */
    ValidationResponse validateTicket(ValidateTicketRequest request);

    /**
     * Récupérer l'historique de validation d'un ticket
     */
    List<ValidationResponse> getTicketValidationHistory(Long ticketId);

    /**
     * Vérifier si un ticket a déjà été validé
     */
    boolean isTicketAlreadyValidated(Long ticketId);
}