package com.transport.ticket.repository;

import com.transport.ticket.model.Ticket;
import com.transport.ticket.model.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // ==================== RECHERCHES DE BASE ====================

    /**
     * Trouver tous les tickets d'un passager
     * Utilisé pour : Afficher l'historique des tickets d'un utilisateur
     */
    List<Ticket> findByIdPassager(Long idPassager);

    /**
     * Trouver les tickets d'un passager avec pagination
     * Utilisé pour : Interface utilisateur paginée
     */
    Page<Ticket> findByIdPassager(Long idPassager, Pageable pageable);

    /**
     * Trouver un ticket par son numéro unique
     * Utilisé pour : Validation via QR code
     */
    Optional<Ticket> findByTicketNumber(String ticketNumber);

    /**
     * Vérifier si un ticket existe par son numéro
     * Utilisé pour : Éviter les doublons
     */
    boolean existsByTicketNumber(String ticketNumber);

    // ==================== RECHERCHES PAR STATUT ====================

    /**
     * Trouver les tickets par statut
     * Utilisé pour : Dashboard admin (voir tous les tickets actifs/expirés/etc.)
     */
    List<Ticket> findByStatut(TicketStatus statut);

    /**
     * Trouver les tickets d'un passager avec un statut spécifique
     * Utilisé pour : Afficher uniquement les tickets actifs d'un utilisateur
     */
    List<Ticket> findByIdPassagerAndStatut(Long idPassager, TicketStatus statut);

    /**
     * Compter les tickets actifs d'un passager
     * Utilisé pour : Limiter le nombre de tickets actifs par utilisateur
     */
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.idPassager = :idPassager AND t.statut = :statut")
    long countByIdPassagerAndStatut(@Param("idPassager") Long idPassager, @Param("statut") TicketStatus statut);

    // ==================== RECHERCHES PAR DATE ====================

    /**
     * Trouver les tickets expirés (validUntil < maintenant ET statut = ACTIVE)
     * Utilisé pour : Job automatique qui met à jour les statuts expirés
     */
    @Query("SELECT t FROM Ticket t WHERE t.validUntil < :currentDate AND t.statut = 'ACTIVE'")
    List<Ticket> findExpiredTickets(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Trouver les tickets qui expirent bientôt (dans les prochaines 24h)
     * Utilisé pour : Envoyer des notifications aux utilisateurs
     */
    @Query("SELECT t FROM Ticket t WHERE t.validUntil BETWEEN :now AND :tomorrow AND t.statut = 'ACTIVE'")
    List<Ticket> findTicketsExpiringSoon(
            @Param("now") LocalDateTime now,
            @Param("tomorrow") LocalDateTime tomorrow
    );

    /**
     * Trouver les tickets achetés entre deux dates
     * Utilisé pour : Rapports et statistiques
     */
    @Query("SELECT t FROM Ticket t WHERE t.dateAchat BETWEEN :startDate AND :endDate")
    List<Ticket> findTicketsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // ==================== RECHERCHES PAR TRAJET ====================

    /**
     * Trouver les tickets pour un trajet spécifique
     * Utilisé pour : Statistiques par ligne de bus
     */
    List<Ticket> findByIdTrajet(Long idTrajet);

    /**
     * Compter les tickets vendus pour un trajet
     * Utilisé pour : Analytics des lignes les plus populaires
     */
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.idTrajet = :idTrajet AND t.statut != 'CANCELLED'")
    long countTicketsByTrajet(@Param("idTrajet") Long idTrajet);

    // ==================== STATISTIQUES ====================

    /**
     * Compter les tickets par statut
     * Utilisé pour : Dashboard admin (graphiques)
     */
    @Query("SELECT t.statut, COUNT(t) FROM Ticket t GROUP BY t.statut")
    List<Object[]> countTicketsByStatus();

    /**
     * Calculer le revenu total des tickets vendus aujourd'hui
     * Utilisé pour : Dashboard financier
     */
    @Query("SELECT COALESCE(SUM(t.prix), 0) FROM Ticket t WHERE CAST(t.dateAchat AS DATE) = CURRENT_DATE AND t.statut != 'CANCELLED'")
    BigDecimal calculateTodayRevenue();

    /**
     * Trouver les tickets les plus récents
     * Utilisé pour : Afficher les dernières ventes
     */
    List<Ticket> findTop10ByOrderByDateAchatDesc();

    // ==================== VALIDITÉ ====================

    /**
     * Vérifier si un ticket est encore valide
     * Utilisé pour : Validation en temps réel
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Ticket t " +
            "WHERE t.ticketNumber = :ticketNumber AND t.statut = 'ACTIVE' " +
            "AND t.validFrom <= :now AND t.validUntil > :now")
    boolean isTicketValid(
            @Param("ticketNumber") String ticketNumber,
            @Param("now") LocalDateTime now
    );

    // ==================== STATISTIQUES ADMIN ====================

    /**
     * Compter les tickets par statut spécifique
     * Utilisé pour : Statistiques du dashboard admin
     */
    long countByStatut(TicketStatus statut);

    /**
     * Calculer le revenu total de tous les tickets
     * Utilisé pour : Dashboard financier
     */
    @Query("SELECT COALESCE(SUM(t.prix), 0.0) FROM Ticket t WHERE t.statut != 'CANCELLED'")
    Double sumTotalRevenue();

    /**
     * Calculer le revenu par statut de ticket
     * Utilisé pour : Statistiques financières détaillées
     */
    @Query("SELECT COALESCE(SUM(t.prix), 0.0) FROM Ticket t WHERE t.statut = :statut")
    Double sumRevenueByStatus(@Param("statut") TicketStatus statut);
}