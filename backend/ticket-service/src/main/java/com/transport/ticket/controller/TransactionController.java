package com.transport.ticket.controller;

import com.transport.ticket.dto.response.TransactionResponse;
import com.transport.ticket.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST pour gérer les transactions
 * Expose tous les endpoints liés aux paiements et transactions
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 🔍 Récupérer une transaction par son ID
     * GET /api/transactions/{id}
     *
     * Exemple: GET /api/transactions/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {

        log.info("📥 [GET /api/transactions/{}] Requête reçue", id);

        TransactionResponse response = transactionService.getTransactionById(id);

        log.info("📤 [GET /api/transactions/{}] Transaction trouvée: {}",
                id, response.getTransactionReference());

        return ResponseEntity.ok(response);
    }

    /**
     * 🔍 Récupérer une transaction par sa référence
     * GET /api/transactions/reference/{reference}
     *
     * Exemple: GET /api/transactions/reference/TXN-1234567890
     */
    @GetMapping("/reference/{reference}")
    public ResponseEntity<TransactionResponse> getTransactionByReference(
            @PathVariable String reference) {

        log.info("📥 [GET /api/transactions/reference/{}] Requête reçue", reference);

        TransactionResponse response = transactionService.getTransactionByReference(reference);

        log.info("📤 [GET /api/transactions/reference/{}] Transaction trouvée - ID: {}",
                reference, response.getIdTransaction());

        return ResponseEntity.ok(response);
    }

    /**
     * 📋 Récupérer toutes les transactions d'un ticket
     * GET /api/transactions/ticket/{ticketId}
     *
     * Exemple: GET /api/transactions/ticket/1
     */
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<TransactionResponse>> getTicketTransactions(
            @PathVariable Long ticketId) {

        log.info("📥 [GET /api/transactions/ticket/{}] Requête reçue", ticketId);

        List<TransactionResponse> transactions = transactionService.getTicketTransactions(ticketId);

        log.info("📤 [GET /api/transactions/ticket/{}] {} transaction(s) trouvée(s)",
                ticketId, transactions.size());

        return ResponseEntity.ok(transactions);
    }

    /**
     * 📋 Récupérer toutes les transactions
     * GET /api/transactions
     *
     * Exemple: GET /api/transactions
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {

        log.info("📥 [GET /api/transactions] Requête reçue");

        List<TransactionResponse> transactions = transactionService.getAllTransactions();

        log.info("📤 [GET /api/transactions] {} transaction(s) trouvée(s)", transactions.size());

        return ResponseEntity.ok(transactions);
    }

    /**
     * 📋 Récupérer les transactions par statut
     * GET /api/transactions/status/{status}
     *
     * Statuts possibles: SUCCESS, PENDING, FAILED, REFUNDED
     * Exemple: GET /api/transactions/status/SUCCESS
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByStatus(
            @PathVariable String status) {

        log.info("📥 [GET /api/transactions/status/{}] Requête reçue", status);

        List<TransactionResponse> transactions = transactionService.getTransactionsByStatus(status);

        log.info("📤 [GET /api/transactions/status/{}] {} transaction(s) trouvée(s)",
                status, transactions.size());

        return ResponseEntity.ok(transactions);
    }

    /**
     * 💰 Calculer le revenu du jour
     * GET /api/transactions/revenue/today
     *
     * Exemple: GET /api/transactions/revenue/today
     */
    @GetMapping("/revenue/today")
    public ResponseEntity<Map<String, Object>> getTodayRevenue() {

        log.info("📥 [GET /api/transactions/revenue/today] Requête reçue");

        BigDecimal revenue = transactionService.getTodayRevenue();

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDateTime.now().toLocalDate());
        response.put("revenue", revenue);
        response.put("currency", "MAD");

        log.info("📤 [GET /api/transactions/revenue/today] Revenu: {} MAD", revenue);

        return ResponseEntity.ok(response);
    }

    /**
     * 💰 Calculer le revenu entre deux dates
     * GET /api/transactions/revenue/period?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
     *
     * Paramètres:
     * - startDate: date de début (format: yyyy-MM-ddTHH:mm:ss)
     * - endDate: date de fin (format: yyyy-MM-ddTHH:mm:ss)
     *
     * Exemple: GET /api/transactions/revenue/period?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
     */
    @GetMapping("/revenue/period")
    public ResponseEntity<Map<String, Object>> getRevenueBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("📥 [GET /api/transactions/revenue/period] Requête reçue - Du {} au {}",
                startDate, endDate);

        BigDecimal revenue = transactionService.getRevenueBetweenDates(startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("revenue", revenue);
        response.put("currency", "MAD");

        log.info("📤 [GET /api/transactions/revenue/period] Revenu: {} MAD", revenue);

        return ResponseEntity.ok(response);
    }

    /**
     * 📊 Obtenir les statistiques des transactions
     * GET /api/transactions/statistics
     *
     * Retourne:
     * - Nombre total de transactions
     * - Nombre de transactions réussies
     * - Nombre de transactions échouées
     * - Taux de réussite
     * - Revenu du jour
     * - Montant moyen
     * - Répartition par statut
     * - Répartition par méthode de paiement
     *
     * Exemple: GET /api/transactions/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTransactionStatistics() {

        log.info("📥 [GET /api/transactions/statistics] Requête reçue");

        Map<String, Object> statistics = transactionService.getTransactionStatistics();

        log.info("📤 [GET /api/transactions/statistics] Statistiques calculées");

        return ResponseEntity.ok(statistics);
    }

    /**
     * 💚 Health check de l'API Transactions
     * GET /api/transactions/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Transaction Service");
        response.put("message", "Le service de transactions fonctionne correctement");

        return ResponseEntity.ok(response);
    }
}