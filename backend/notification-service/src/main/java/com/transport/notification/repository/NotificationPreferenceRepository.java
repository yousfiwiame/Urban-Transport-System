package com.transport.notification.repository;

import com.transport.notification.model.UserNotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for UserNotificationPreference entity.
 */
@Repository
public interface NotificationPreferenceRepository extends JpaRepository<UserNotificationPreference, Integer> {

    /**
     * Finds notification preferences for a specific user.
     * 
     * @param userId the user ID
     * @return optional preference
     */
    Optional<UserNotificationPreference> findByUserId(Integer userId);
}

