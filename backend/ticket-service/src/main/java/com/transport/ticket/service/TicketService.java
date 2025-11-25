package com.transport.ticket.service;

import com.transport.ticket.dto.request.PurchaseTicketRequest;
import com.transport.ticket.dto.response.PurchaseTicketResponse;
import com.transport.ticket.dto.response.TicketResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

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

    /**
     * Récupérer tous les tickets (admin) avec pagination
     */
    Page<TicketResponse> getAllTickets(Pageable pageable);

    /**
     * Récupérer les statistiques des tickets
     */
    Map<String, Object> getTicketStatistics();

    /**
     * Générer un PDF pour un ticket
     */
    byte[] generateTicketPDF(Long ticketId) throws Exception;

    /**
     * Créer un ticket (ADMIN uniquement)
     */
    TicketResponse createTicketByAdmin(com.transport.ticket.dto.request.CreateTicketRequest request);

    /**
     * Mettre à jour un ticket (ADMIN uniquement)
     */
    TicketResponse updateTicketByAdmin(Long ticketId, com.transport.ticket.dto.request.UpdateTicketRequest request);
}