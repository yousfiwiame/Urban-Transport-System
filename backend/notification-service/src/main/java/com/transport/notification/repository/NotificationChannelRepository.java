package com.transport.notification.repository;

import com.transport.notification.model.NotificationChannel;
import com.transport.notification.model.enums.ChannelStatus;
import com.transport.notification.model.enums.ChannelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for NotificationChannel entity.
 */
@Repository
public interface NotificationChannelRepository extends JpaRepository<NotificationChannel, Integer> {

    /**
     * Finds all channels for a specific notification.
     * 
     * @param notificationId the notification ID
     * @return list of channels
     */
    List<NotificationChannel> findByNotification_NotificationId(Integer notificationId);

    /**
     * Finds a channel by notification ID and channel type.
     * 
     * @param notificationId the notification ID
     * @param channelType the channel type
     * @return optional channel
     */
    Optional<NotificationChannel> findByNotification_NotificationIdAndChannelType(
            Integer notificationId, 
            ChannelType channelType
    );

    /**
     * Finds channels that need retry.
     * 
     * @param status the channel status
     * @param now current timestamp
     * @return list of channels ready for retry
     */
    @Query("SELECT nc FROM NotificationChannel nc WHERE nc.channelStatus = :status " +
           "AND nc.nextRetryAt IS NOT NULL AND nc.nextRetryAt <= :now")
    List<NotificationChannel> findChannelsReadyForRetry(
            @Param("status") ChannelStatus status,
            @Param("now") OffsetDateTime now
    );

    /**
     * Finds channels by status.
     * 
     * @param status the channel status
     * @return list of channels
     */
    List<NotificationChannel> findByChannelStatus(ChannelStatus status);
}

