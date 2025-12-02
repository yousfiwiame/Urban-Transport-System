package com.transport.notification.repository;

import com.transport.notification.model.Notification;
import com.transport.notification.model.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository interface for Notification entity.
 * 
 * <p>Provides data access methods for notification operations including
 * queries by user, status, date ranges, and event relationships.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    /**
     * Finds all notifications for a specific user.
     * 
     * @param userId the user ID
     * @param pageable pagination parameters
     * @return page of notifications
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    /**
     * Finds notifications by status.
     * 
     * @param status the notification status
     * @return list of notifications with the specified status
     */
    List<Notification> findByStatus(NotificationStatus status);

    /**
     * Finds notifications by user ID and status.
     * 
     * @param userId the user ID
     * @param status the notification status
     * @param pageable pagination parameters
     * @return page of notifications
     */
    Page<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(
            Integer userId, 
            NotificationStatus status, 
            Pageable pageable
    );

    /**
     * Finds pending notifications that are scheduled to be sent.
     * 
     * @param now current timestamp
     * @return list of notifications ready to be sent
     */
    @Query("SELECT n FROM Notification n WHERE n.status = :status " +
           "AND (n.scheduledAt IS NULL OR n.scheduledAt <= :now)")
    List<Notification> findPendingNotificationsReadyToSend(
            @Param("status") NotificationStatus status,
            @Param("now") OffsetDateTime now
    );

    /**
     * Counts unread notifications for a user.
     * 
     * @param userId the user ID
     * @return count of unread notifications
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId " +
           "AND n.status != 'READ'")
    Long countUnreadByUserId(@Param("userId") Integer userId);

    /**
     * Finds notifications created within a date range.
     * 
     * @param startDate start of date range
     * @param endDate end of date range
     * @return list of notifications
     */
    List<Notification> findByCreatedAtBetween(OffsetDateTime startDate, OffsetDateTime endDate);
}

