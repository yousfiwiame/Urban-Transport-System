package com.transport.ticket.service;

import com.transport.ticket.dto.request.PurchaseTicketRequest;
import com.transport.ticket.dto.response.PurchaseTicketResponse;
import com.transport.ticket.dto.response.TicketResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface du service Ticket
 */
public interface TicketService {

    /**
     * Acheter un nouveau ticket
     */
    PurchaseTicketResponse purchaseTicket(PurchaseTicketRequest request);

    /**
     * Récupérer un ticket par son ID
     */
    TicketResponse getTicketById(Long ticketId);

    /**
     * Récupérer un ticket par son numéro
     */
    TicketResponse getTicketByNumber(String ticketNumber);

    /**
     * Récupérer tous les tickets d'un passager
     */
    List<TicketResponse> getPassengerTickets(Long passagerId);

    /**
     * Récupérer les tickets d'un passager avec pagination
     */
    Page<TicketResponse> getPassengerTicketsPaginated(Long passagerId, Pageable pageable);

    /**
     * Annuler un ticket
     */
    TicketResponse cancelTicket(Long ticketId, String reason);

    /**
     * Vérifier si un ticket est valide
     */
    boolean isTicketValid(String ticketNumber);

    /**
     * Compter les tickets actifs d'un passager
     */
    long countActiveTickets(Long passagerId);
}