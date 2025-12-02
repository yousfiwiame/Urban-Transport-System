package com.transport.notification.service;

import com.transport.notification.dto.request.SendNotificationRequest;
import com.transport.notification.dto.response.NotificationResponse;
import com.transport.notification.model.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for notification management.
 */
public interface NotificationService {

    /**
     * Sends a notification to a user.
     * 
     * @param request notification request
     * @return notification response
     */
    NotificationResponse sendNotification(SendNotificationRequest request);

    /**
     * Gets notifications for a user.
     * 
     * @param userId user ID
     * @param pageable pagination parameters
     * @return page of notifications
     */
    Page<NotificationResponse> getUserNotifications(Integer userId, Pageable pageable);

    /**
     * Gets notifications by status for a user.
     * 
     * @param userId user ID
     * @param status notification status
     * @param pageable pagination parameters
     * @return page of notifications
     */
    Page<NotificationResponse> getUserNotificationsByStatus(Integer userId, NotificationStatus status, Pageable pageable);

    /**
     * Marks a notification as read.
     * 
     * @param notificationId notification ID
     * @param userId user ID (for authorization)
     * @return updated notification response
     */
    NotificationResponse markAsRead(Integer notificationId, Integer userId);

    /**
     * Gets unread notification count for a user.
     * 
     * @param userId user ID
     * @return count of unread notifications
     */
    Long getUnreadCount(Integer userId);

    /**
     * Processes pending notifications and sends them.
     */
    void processPendingNotifications();
}

