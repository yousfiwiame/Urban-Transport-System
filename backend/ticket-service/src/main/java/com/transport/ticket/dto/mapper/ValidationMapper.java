package com.transport.ticket.dto.mapper;

import com.transport.ticket.dto.response.ValidationResponse;
import com.transport.ticket.model.TicketValidation;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir TicketValidation (Entity) ↔️ ValidationResponse (DTO)
 */
@Component
public class ValidationMapper {

    /**
     * Convertir TicketValidation → ValidationResponse
     */
    public ValidationResponse toResponse(TicketValidation validation, String ticketNumber) {
        if (validation == null) {
            return null;
        }

        return ValidationResponse.builder()
                .idValidation(validation.getIdValidation())
                .ticketId(validation.getTicketId())
                .ticketNumber(ticketNumber)
                .dateValidation(validation.getDateValidation())
                .heureValidation(validation.getHeureValidation())
                .validationTimestamp(validation.getValidationTimestamp())
                .validationLocation(validation.getValidationLocation())
                .validatorId(validation.getValidatorId())
                .success(true)
                .message("Ticket validé avec succès")
                .build();
    }

    /**
     * Créer une ValidationResponse en cas d'échec
     */
    public ValidationResponse toErrorResponse(String ticketNumber, String errorMessage) {
        return ValidationResponse.builder()
                .ticketNumber(ticketNumber)
                .success(false)
                .message(errorMessage)
                .build();
    }
}