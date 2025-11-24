package com.transport.notification.repository;

import com.transport.notification.model.NotificationTemplate;
import com.transport.notification.model.enums.ChannelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for NotificationTemplate entity.
 */
@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Integer> {

    /**
     * Finds a template by its unique code.
     * 
     * @param templateCode the template code
     * @return optional template
     */
    Optional<NotificationTemplate> findByTemplateCode(String templateCode);

    /**
     * Finds all active templates for a specific channel type.
     * 
     * @param channelType the channel type
     * @param isActive whether the template is active
     * @return list of templates
     */
    List<NotificationTemplate> findByChannelTypeAndIsActive(ChannelType channelType, Boolean isActive);

    /**
     * Finds all active templates.
     * 
     * @param isActive whether the template is active
     * @return list of active templates
     */
    List<NotificationTemplate> findByIsActive(Boolean isActive);
}

