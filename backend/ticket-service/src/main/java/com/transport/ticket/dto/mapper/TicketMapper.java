package com.transport.ticket.dto.mapper;

import com.transport.ticket.dto.response.TicketResponse;
import com.transport.ticket.model.Ticket;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Mapper pour convertir Ticket (Entity) ↔️ TicketResponse (DTO)
 */
@Component
public class TicketMapper {

    /**
     * Convertir Ticket → TicketResponse
     */
    public TicketResponse toResponse(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        boolean isExpired = ticket.getValidUntil() != null && ticket.getValidUntil().isBefore(now);
        boolean isValid = !isExpired && "ACTIVE".equals(ticket.getStatut().name());

        Long remainingMinutes = null;
        if (ticket.getValidUntil() != null && !isExpired) {
            remainingMinutes = Duration.between(now, ticket.getValidUntil()).toMinutes();
        }

        return TicketResponse.builder()
                .idTicket(ticket.getIdTicket())
                .ticketNumber(ticket.getTicketNumber())
                .idPassager(ticket.getIdPassager())
                .idTrajet(ticket.getIdTrajet())
                .prix(ticket.getPrix())
                .dateAchat(ticket.getDateAchat())
                .statut(ticket.getStatut().name())
                .qrCode(ticket.getQrCode())
                .validFrom(ticket.getValidFrom())
                .validUntil(ticket.getValidUntil())
                .isExpired(isExpired)
                .isValid(isValid)
                .remainingTimeInMinutes(remainingMinutes)
                .build();
    }

    /**
     * Convertir TicketResponse → Ticket
     * (Rarement utilisé, mais utile pour les tests)
     */
    public Ticket toEntity(TicketResponse response) {
        if (response == null) {
            return null;
        }

        Ticket ticket = new Ticket();
        ticket.setIdTicket(response.getIdTicket());
        ticket.setTicketNumber(response.getTicketNumber());
        ticket.setIdPassager(response.getIdPassager());
        ticket.setIdTrajet(response.getIdTrajet());
        ticket.setPrix(response.getPrix());
        ticket.setDateAchat(response.getDateAchat());
        ticket.setQrCode(response.getQrCode());
        ticket.setValidFrom(response.getValidFrom());
        ticket.setValidUntil(response.getValidUntil());

        return ticket;
    }
}