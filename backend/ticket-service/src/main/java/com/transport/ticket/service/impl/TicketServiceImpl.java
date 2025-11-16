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
import com.transport.ticket.util.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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

            // 2. Générer le QR code
            String qrCode = qrCodeGenerator.generateQRCode(ticket.getTicketNumber());
            ticket.setQrCode(qrCode);
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
}