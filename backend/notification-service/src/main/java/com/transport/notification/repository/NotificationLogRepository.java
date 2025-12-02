package com.transport.notification.repository;

import com.transport.notification.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for NotificationLog entity.
 */
@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Integer> {

    /**
     * Finds all logs for a specific notification.
     * 
     * @param notificationId the notification ID
     * @return list of logs ordered by timestamp
     */
    List<NotificationLog> findByNotification_NotificationIdOrderByLoggedAtDesc(Integer notificationId);
}

