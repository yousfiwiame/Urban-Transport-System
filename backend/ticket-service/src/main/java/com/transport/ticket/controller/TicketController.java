package com.transport.ticket.controller;

import com.transport.ticket.dto.request.PurchaseTicketRequest;
import com.transport.ticket.dto.response.PurchaseTicketResponse;
import com.transport.ticket.dto.response.TicketResponse;
import com.transport.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST pour gérer les tickets
 * Expose tous les endpoints liés aux tickets
 */
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Pour autoriser les requêtes depuis le frontend
public class TicketController {

    private final TicketService ticketService;

    /**
     * 🎫 Acheter un nouveau ticket
     * POST /api/tickets/purchase
     *
     * Body JSON exemple:
     * {
     *   "idPassager": 1,
     *   "idTrajet": 5,
     *   "prix": 15.50,
     *   "methodePaiement": "CREDIT_CARD"
     * }
     */
    @PostMapping("/purchase")
    public ResponseEntity<PurchaseTicketResponse> purchaseTicket(
            @Valid @RequestBody PurchaseTicketRequest request) {

        log.info("📥 [POST /api/tickets/purchase] Requête reçue pour le passager ID: {}",
                request.getIdPassager());

        PurchaseTicketResponse response = ticketService.purchaseTicket(request);

        log.info("📤 [POST /api/tickets/purchase] Réponse envoyée - Ticket ID: {}",
                response.getTicket().getIdTicket());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 🔍 Récupérer un ticket par son ID
     * GET /api/tickets/{id}
     *
     * Exemple: GET /api/tickets/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {

        log.info("📥 [GET /api/tickets/{}] Requête reçue", id);

        TicketResponse response = ticketService.getTicketById(id);

        log.info("📤 [GET /api/tickets/{}] Ticket trouvé: {}", id, response.getTicketNumber());

        return ResponseEntity.ok(response);
    }

    /**
     * 🔍 Récupérer un ticket par son numéro
     * GET /api/tickets/number/{ticketNumber}
     *
     * Exemple: GET /api/tickets/number/TKT-1234567890
     */
    @GetMapping("/number/{ticketNumber}")
    public ResponseEntity<TicketResponse> getTicketByNumber(@PathVariable String ticketNumber) {

        log.info("📥 [GET /api/tickets/number/{}] Requête reçue", ticketNumber);

        TicketResponse response = ticketService.getTicketByNumber(ticketNumber);

        log.info("📤 [GET /api/tickets/number/{}] Ticket trouvé - ID: {}",
                ticketNumber, response.getIdTicket());

        return ResponseEntity.ok(response);
    }

    /**
     * 📋 Récupérer tous les tickets d'un passager
     * GET /api/tickets/passager/{passagerId}
     *
     * Exemple: GET /api/tickets/passager/1
     */
    @GetMapping("/passager/{passagerId}")
    public ResponseEntity<List<TicketResponse>> getPassengerTickets(
            @PathVariable Long passagerId) {

        log.info("📥 [GET /api/tickets/passager/{}] Requête reçue", passagerId);

        List<TicketResponse> tickets = ticketService.getPassengerTickets(passagerId);

        log.info("📤 [GET /api/tickets/passager/{}] {} ticket(s) trouvé(s)",
                passagerId, tickets.size());

        return ResponseEntity.ok(tickets);
    }

    /**
     * 📄 Récupérer les tickets d'un passager avec pagination
     * GET /api/tickets/passager/{passagerId}/paginated?page=0&size=10&sort=dateAchat,desc
     *
     * Paramètres:
     * - page: numéro de la page (commence à 0)
     * - size: nombre d'éléments par page
     * - sort: critère de tri (ex: dateAchat,desc)
     *
     * Exemple: GET /api/tickets/passager/1/paginated?page=0&size=5
     */
    @GetMapping("/passager/{passagerId}/paginated")
    public ResponseEntity<Page<TicketResponse>> getPassengerTicketsPaginated(
            @PathVariable Long passagerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateAchat") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        log.info("📥 [GET /api/tickets/passager/{}/paginated] Requête reçue - page: {}, size: {}",
                passagerId, page, size);

        // Créer l'objet Pageable
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<TicketResponse> ticketsPage = ticketService.getPassengerTicketsPaginated(
                passagerId, pageable);

        log.info("📤 [GET /api/tickets/passager/{}/paginated] Page {}/{} - {} ticket(s)",
                passagerId,
                ticketsPage.getNumber() + 1,
                ticketsPage.getTotalPages(),
                ticketsPage.getNumberOfElements());

        return ResponseEntity.ok(ticketsPage);
    }

    /**
     * ❌ Annuler un ticket
     * DELETE /api/tickets/{id}
     *
     * Query param: reason (optionnel)
     * Exemple: DELETE /api/tickets/1?reason=Changement de plans
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<TicketResponse> cancelTicket(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Annulation demandée par l'utilisateur") String reason) {

        log.info("📥 [DELETE /api/tickets/{}] Requête d'annulation - Raison: {}", id, reason);

        TicketResponse response = ticketService.cancelTicket(id, reason);

        log.info("📤 [DELETE /api/tickets/{}] Ticket annulé avec succès", id);

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ Vérifier si un ticket est valide
     * GET /api/tickets/check-validity/{ticketNumber}
     *
     * Exemple: GET /api/tickets/check-validity/TKT-1234567890
     */
    @GetMapping("/check-validity/{ticketNumber}")
    public ResponseEntity<Map<String, Object>> checkTicketValidity(
            @PathVariable String ticketNumber) {

        log.info("📥 [GET /api/tickets/check-validity/{}] Requête reçue", ticketNumber);

        boolean isValid = ticketService.isTicketValid(ticketNumber);

        Map<String, Object> response = new HashMap<>();
        response.put("ticketNumber", ticketNumber);
        response.put("isValid", isValid);
        response.put("message", isValid
                ? "Le ticket est valide"
                : "Le ticket n'est pas valide ou a expiré");

        log.info("📤 [GET /api/tickets/check-validity/{}] Ticket est {}",
                ticketNumber, isValid ? "valide" : "invalide");

        return ResponseEntity.ok(response);
    }

    /**
     * 🔢 Compter les tickets actifs d'un passager
     * GET /api/tickets/passager/{passagerId}/active-count
     *
     * Exemple: GET /api/tickets/passager/1/active-count
     */
    @GetMapping("/passager/{passagerId}/active-count")
    public ResponseEntity<Map<String, Object>> countActiveTickets(
            @PathVariable Long passagerId) {

        log.info("📥 [GET /api/tickets/passager/{}/active-count] Requête reçue", passagerId);

        long count = ticketService.countActiveTickets(passagerId);

        Map<String, Object> response = new HashMap<>();
        response.put("passagerId", passagerId);
        response.put("activeTicketsCount", count);

        log.info("📤 [GET /api/tickets/passager/{}/active-count] {} ticket(s) actif(s)",
                passagerId, count);

        return ResponseEntity.ok(response);
    }

    /**
     * 💚 Health check de l'API Tickets
     * GET /api/tickets/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Ticket Service");
        response.put("message", "Le service de tickets fonctionne correctement");

        return ResponseEntity.ok(response);
    }
}