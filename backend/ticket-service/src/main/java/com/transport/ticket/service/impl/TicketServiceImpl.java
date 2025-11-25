package com.transport.ticket.service.impl;

import com.transport.ticket.dto.mapper.TicketMapper;
import com.transport.ticket.dto.mapper.TransactionMapper;
import com.transport.ticket.dto.request.PurchaseTicketRequest;
import com.transport.ticket.dto.response.PurchaseTicketResponse;
import com.transport.ticket.dto.response.TicketResponse;
import com.transport.ticket.dto.response.TransactionResponse;
import com.transport.ticket.exception.TicketNotFoundException;
import com.transport.ticket.exception.InvalidTicketException;
import com.transport.ticket.model.*;
import com.transport.ticket.repository.TicketRepository;
import com.transport.ticket.repository.TransactionRepository;
import com.transport.ticket.service.TicketService;
import com.transport.ticket.service.QRCodeService;
import com.transport.ticket.util.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implémentation du service Ticket
 * Contient toute la logique métier pour gérer les tickets
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TransactionRepository transactionRepository;
    private final TicketMapper ticketMapper;
    private final TransactionMapper transactionMapper;
    private final QRCodeGenerator qrCodeGenerator;
    private final QRCodeService qrCodeService;
    private final com.transport.ticket.service.TicketPDFService ticketPDFService;

    @Override
    public PurchaseTicketResponse purchaseTicket(PurchaseTicketRequest request) {
        log.info("🎫 Début de l'achat de ticket pour le passager ID: {}", request.getIdPassager());

        try {
            // 1. Créer le ticket
            Ticket ticket = Ticket.builder()
                    .idPassager(request.getIdPassager())
                    .idTrajet(request.getIdTrajet())
                    .prix(request.getPrix())
                    .statut(TicketStatus.ACTIVE)
                    .build();

            // Le ticketNumber, validFrom et validUntil sont générés automatiquement par @PrePersist
            ticket = ticketRepository.save(ticket);
            log.info("✅ Ticket créé avec succès - ID: {}, Numéro: {}",
                    ticket.getIdTicket(), ticket.getTicketNumber());

            // 2. Générer le QR code avec une vraie image
            String qrData = qrCodeService.generateTicketData(
                ticket.getIdTicket(),
                ticket.getIdPassager(),
                "Route-" + ticket.getIdTrajet(),
                ticket.getDateAchat().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
            byte[] qrCodeBytes = qrCodeService.generateQRCode(qrData);
            
            // Convertir en Data URL pour le frontend
            String qrCodeDataUrl = "data:image/png;base64," + 
                java.util.Base64.getEncoder().encodeToString(qrCodeBytes);
            ticket.setQrCode(qrCodeDataUrl);
            ticket = ticketRepository.save(ticket);
            log.info("🔲 QR code généré pour le ticket: {}", ticket.getTicketNumber());

            // 3. Créer la transaction
            Transaction transaction = Transaction.builder()
                    .ticketId(ticket.getIdTicket())
                    .montant(request.getPrix())
                    .statut(PaymentStatus.COMPLETED)
                    .methodePaiement(PaymentMethod.valueOf(request.getMethodePaiement()))
                    .description("Achat de ticket " + ticket.getTicketNumber())
                    .build();

            // transactionReference et dateTransaction sont générés par @PrePersist
            transaction = transactionRepository.save(transaction);
            log.info("💳 Transaction créée avec succès - Référence: {}",
                    transaction.getTransactionReference());

            // 4. Convertir en DTOs
            TicketResponse ticketResponse = ticketMapper.toResponse(ticket);
            TransactionResponse transactionResponse = transactionMapper.toResponse(transaction);

            // 5. Créer la réponse combinée
            PurchaseTicketResponse response = PurchaseTicketResponse.builder()
                    .ticket(ticketResponse)
                    .transaction(transactionResponse)
                    .success(true)
                    .message("Ticket acheté avec succès ! Valide jusqu'au " + ticket.getValidUntil())
                    .build();

            log.info("🎉 Achat de ticket terminé avec succès pour le passager ID: {}",
                    request.getIdPassager());

            return response;

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'achat du ticket: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'achat du ticket: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long ticketId) {
        log.info("🔍 Recherche du ticket avec ID: {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    log.error("❌ Ticket non trouvé avec ID: {}", ticketId);
                    return new TicketNotFoundException("Ticket non trouvé avec l'ID: " + ticketId);
                });

        log.info("✅ Ticket trouvé: {}", ticket.getTicketNumber());
        return ticketMapper.toResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse getTicketByNumber(String ticketNumber) {
        log.info("🔍 Recherche du ticket avec numéro: {}", ticketNumber);

        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> {
                    log.error("❌ Ticket non trouvé avec numéro: {}", ticketNumber);
                    return new TicketNotFoundException("Ticket non trouvé avec le numéro: " + ticketNumber);
                });

        log.info("✅ Ticket trouvé: ID {}", ticket.getIdTicket());
        return ticketMapper.toResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getPassengerTickets(Long passagerId) {
        log.info("🔍 Recherche de tous les tickets du passager ID: {}", passagerId);

        List<Ticket> tickets = ticketRepository.findByIdPassager(passagerId);

        log.info("✅ {} ticket(s) trouvé(s) pour le passager ID: {}", tickets.size(), passagerId);

        return tickets.stream()
                .map(ticketMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponse> getPassengerTicketsPaginated(Long passagerId, Pageable pageable) {
        log.info("🔍 Recherche paginée des tickets du passager ID: {} (page: {}, size: {})",
                passagerId, pageable.getPageNumber(), pageable.getPageSize());

        Page<Ticket> ticketsPage = ticketRepository.findByIdPassager(passagerId, pageable);

        log.info("✅ Page {}/{} trouvée avec {} ticket(s)",
                ticketsPage.getNumber() + 1,
                ticketsPage.getTotalPages(),
                ticketsPage.getNumberOfElements());

        return ticketsPage.map(ticketMapper::toResponse);
    }

    @Override
    public TicketResponse cancelTicket(Long ticketId, String reason) {
        log.info("🚫 Annulation du ticket ID: {} - Raison: {}", ticketId, reason);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket non trouvé avec l'ID: " + ticketId));

        // Vérifier si le ticket peut être annulé
        if (ticket.getStatut() == TicketStatus.USED) {
            log.error("❌ Impossible d'annuler un ticket déjà utilisé");
            throw new InvalidTicketException("Impossible d'annuler un ticket déjà utilisé");
        }

        if (ticket.getStatut() == TicketStatus.CANCELLED) {
            log.error("❌ Le ticket est déjà annulé");
            throw new InvalidTicketException("Le ticket est déjà annulé");
        }

        // Annuler le ticket
        ticket.setStatut(TicketStatus.CANCELLED);
        ticket = ticketRepository.save(ticket);

        log.info("✅ Ticket {} annulé avec succès", ticket.getTicketNumber());

        return ticketMapper.toResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTicketValid(String ticketNumber) {
        log.info("🔍 Vérification de la validité du ticket: {}", ticketNumber);

        LocalDateTime now = LocalDateTime.now();
        boolean isValid = ticketRepository.isTicketValid(ticketNumber, now);

        log.info("✅ Ticket {} est {}valide", ticketNumber, isValid ? "" : "IN");

        return isValid;
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveTickets(Long passagerId) {
        log.info("🔢 Comptage des tickets actifs pour le passager ID: {}", passagerId);

        long count = ticketRepository.countByIdPassagerAndStatut(passagerId, TicketStatus.ACTIVE);

        log.info("✅ Le passager ID: {} a {} ticket(s) actif(s)", passagerId, count);

        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponse> getAllTickets(Pageable pageable) {
        log.info("📋 Récupération de tous les tickets (admin) - page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Ticket> ticketsPage = ticketRepository.findAll(pageable);

        log.info("✅ {} ticket(s) récupéré(s) sur {} au total", 
                ticketsPage.getNumberOfElements(), ticketsPage.getTotalElements());

        return ticketsPage.map(ticketMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTicketStatistics() {
        log.info("📊 Calcul des statistiques des tickets");

        Map<String, Object> stats = new HashMap<>();

        // Total des tickets
        long totalTickets = ticketRepository.count();
        stats.put("totalTickets", totalTickets);

        // Tickets par statut
        long activeTickets = ticketRepository.countByStatut(TicketStatus.ACTIVE);
        long usedTickets = ticketRepository.countByStatut(TicketStatus.USED);
        long expiredTickets = ticketRepository.countByStatut(TicketStatus.EXPIRED);
        long cancelledTickets = ticketRepository.countByStatut(TicketStatus.CANCELLED);

        stats.put("activeTickets", activeTickets);
        stats.put("usedTickets", usedTickets);
        stats.put("expiredTickets", expiredTickets);
        stats.put("cancelledTickets", cancelledTickets);

        // Revenus totaux
        Double totalRevenue = ticketRepository.sumTotalRevenue();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);

        // Revenus par statut
        Double activeRevenue = ticketRepository.sumRevenueByStatus(TicketStatus.ACTIVE);
        stats.put("activeRevenue", activeRevenue != null ? activeRevenue : 0.0);

        Double usedRevenue = ticketRepository.sumRevenueByStatus(TicketStatus.USED);
        stats.put("usedRevenue", usedRevenue != null ? usedRevenue : 0.0);

        log.info("✅ Statistiques calculées: {} tickets au total, {} MAD de revenus", 
                totalTickets, stats.get("totalRevenue"));

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateTicketPDF(Long ticketId) throws Exception {
        log.info("📄 Génération du PDF pour le ticket ID: {}", ticketId);

        // Récupérer le ticket
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket introuvable avec l'ID: " + ticketId));

        // TODO: Récupérer les informations de l'utilisateur via Feign Client
        // Pour l'instant, on utilise un nom générique
        String userName = "Passager " + ticket.getIdPassager();

        // TODO: Récupérer les informations de la route via Feign Client
        // Pour l'instant, on utilise un format générique
        String routeInfo = "Route " + ticket.getIdTrajet() + " - Destination";

        // Générer le PDF
        byte[] pdfBytes = ticketPDFService.generateTicketPDF(ticket, userName, routeInfo);

        log.info("✅ PDF généré avec succès pour le ticket ID: {} ({} bytes)", 
                ticketId, pdfBytes.length);

        return pdfBytes;
    }

    @Override
    public TicketResponse createTicketByAdmin(com.transport.ticket.dto.request.CreateTicketRequest request) {
        log.info("🎫 [ADMIN] Création d'un ticket pour le passager ID: {}", request.getIdPassager());

        try {
            // Créer le ticket avec les valeurs fournies ou par défaut
            Ticket ticket = Ticket.builder()
                    .idPassager(request.getIdPassager())
                    .idTrajet(request.getIdTrajet())
                    .prix(request.getPrix())
                    .statut(request.getStatut() != null ? request.getStatut() : TicketStatus.ACTIVE)
                    .build();

            // Si dateAchat est fournie, l'utiliser, sinon @PrePersist le gérera
            if (request.getDateAchat() != null) {
                ticket.setDateAchat(request.getDateAchat());
            }

            // Sauvegarder pour générer le ticketNumber
            ticket = ticketRepository.save(ticket);
            log.info("✅ [ADMIN] Ticket créé - ID: {}, Numéro: {}", 
                    ticket.getIdTicket(), ticket.getTicketNumber());

            // Générer le QR code avec une vraie image
            String qrData = qrCodeService.generateTicketData(
                ticket.getIdTicket(),
                ticket.getIdPassager(),
                "Route-" + ticket.getIdTrajet(),
                ticket.getDateAchat().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
            byte[] qrCodeBytes = qrCodeService.generateQRCode(qrData);
            
            // Convertir en Data URL pour le frontend
            String qrCodeDataUrl = "data:image/png;base64," + 
                java.util.Base64.getEncoder().encodeToString(qrCodeBytes);
            ticket.setQrCode(qrCodeDataUrl);
            
            // Si dateValidite est fournie, l'utiliser
            if (request.getDateValidite() != null) {
                ticket.setValidUntil(request.getDateValidite());
            }
            
            ticket = ticketRepository.save(ticket);
            log.info("🔲 [ADMIN] QR code généré pour le ticket: {}", ticket.getTicketNumber());

            // Créer la transaction associée
            Transaction transaction = Transaction.builder()
                    .ticketId(ticket.getIdTicket())
                    .montant(request.getPrix())
                    .statut(PaymentStatus.COMPLETED)
                    .methodePaiement(PaymentMethod.valueOf(request.getMethodePaiement()))
                    .description("Ticket créé par l'administrateur - " + ticket.getTicketNumber())
                    .build();

            transactionRepository.save(transaction);
            log.info("💳 [ADMIN] Transaction créée pour le ticket: {}", ticket.getTicketNumber());

            return ticketMapper.toResponse(ticket);

        } catch (Exception e) {
            log.error("❌ [ADMIN] Erreur lors de la création du ticket: {}", e.getMessage());
            throw new InvalidTicketException("Erreur lors de la création du ticket: " + e.getMessage());
        }
    }

    @Override
    public TicketResponse updateTicketByAdmin(Long ticketId, com.transport.ticket.dto.request.UpdateTicketRequest request) {
        log.info("✏️ [ADMIN] Modification du ticket ID: {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket introuvable avec l'ID: " + ticketId));

        // Mettre à jour uniquement les champs fournis
        if (request.getIdPassager() != null) {
            ticket.setIdPassager(request.getIdPassager());
            log.info("📝 [ADMIN] ID Passager mis à jour: {}", request.getIdPassager());
        }

        if (request.getIdTrajet() != null) {
            ticket.setIdTrajet(request.getIdTrajet());
            log.info("📝 [ADMIN] ID Trajet mis à jour: {}", request.getIdTrajet());
        }

        if (request.getPrix() != null) {
            ticket.setPrix(request.getPrix());
            log.info("📝 [ADMIN] Prix mis à jour: {}", request.getPrix());
        }

        if (request.getStatut() != null) {
            ticket.setStatut(request.getStatut());
            log.info("📝 [ADMIN] Statut mis à jour: {}", request.getStatut());
        }

        if (request.getDateValidite() != null) {
            ticket.setValidUntil(request.getDateValidite());
            log.info("📝 [ADMIN] Date de validité mise à jour: {}", request.getDateValidite());
        }

        ticket = ticketRepository.save(ticket);
        log.info("✅ [ADMIN] Ticket ID: {} mis à jour avec succès", ticketId);

        return ticketMapper.toResponse(ticket);
    }
}