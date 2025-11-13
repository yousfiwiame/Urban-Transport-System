package com.transport.ticket.controller;

import com.transport.ticket.dto.request.ValidateTicketRequest;
import com.transport.ticket.dto.response.ValidationResponse;
import com.transport.ticket.service.ValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST pour gérer la validation des tickets
 * Utilisé pour scanner les QR codes et valider les tickets
 */
@RestController
@RequestMapping("/api/validations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ValidationController {

    private final ValidationService validationService;

    /**
     * ✅ Valider un ticket (scan QR code)
     * POST /api/validations/validate
     *
     * Body JSON exemple:
     * {
     *   "ticketNumber": "TKT-1234567890",
     *   "validationLocation": "Station Hassan",
     *   "validatorId": 42
     * }
     */
    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validateTicket(
            @Valid @RequestBody ValidateTicketRequest request) {

        log.info("📥 [POST /api/validations/validate] Validation du ticket: {}",
                request.getTicketNumber());

        ValidationResponse response = validationService.validateTicket(request);

        HttpStatus status = response.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        log.info("📤 [POST /api/validations/validate] Résultat: {} - {}",
                response.getSuccess() ? "SUCCESS" : "FAILED",
                response.getMessage());

        return ResponseEntity.status(status).body(response);
    }

    /**
     * 📋 Récupérer l'historique de validation d'un ticket
     * GET /api/validations/ticket/{ticketId}/history
     *
     * Exemple: GET /api/validations/ticket/1/history
     */
    @GetMapping("/ticket/{ticketId}/history")
    public ResponseEntity<List<ValidationResponse>> getValidationHistory(
            @PathVariable Long ticketId) {

        log.info("📥 [GET /api/validations/ticket/{}/history] Requête reçue", ticketId);

        List<ValidationResponse> history = validationService.getTicketValidationHistory(ticketId);

        log.info("📤 [GET /api/validations/ticket/{}/history] {} validation(s) trouvée(s)",
                ticketId, history.size());

        return ResponseEntity.ok(history);
    }

    /**
     * 🔍 Vérifier si un ticket a déjà été validé
     * GET /api/validations/ticket/{ticketId}/is-validated
     *
     * Exemple: GET /api/validations/ticket/1/is-validated
     */
    @GetMapping("/ticket/{ticketId}/is-validated")
    public ResponseEntity<Map<String, Object>> isTicketValidated(
            @PathVariable Long ticketId) {

        log.info("📥 [GET /api/validations/ticket/{}/is-validated] Requête reçue", ticketId);

        boolean isValidated = validationService.isTicketAlreadyValidated(ticketId);

        Map<String, Object> response = new HashMap<>();
        response.put("ticketId", ticketId);
        response.put("isValidated", isValidated);
        response.put("message", isValidated
                ? "Le ticket a déjà été validé"
                : "Le ticket n'a pas encore été validé");

        log.info("📤 [GET /api/validations/ticket/{}/is-validated] Résultat: {}",
                ticketId, isValidated);

        return ResponseEntity.ok(response);
    }

    /**
     * 💚 Health check de l'API Validations
     * GET /api/validations/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Validation Service");
        response.put("message", "Le service de validation fonctionne correctement");

        return ResponseEntity.ok(response);
    }
}