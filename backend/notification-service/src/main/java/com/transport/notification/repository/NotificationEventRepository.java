package com.transport.notification.repository;

import com.transport.notification.model.NotificationEvent;
import com.transport.notification.model.enums.ProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for NotificationEvent entity.
 */
@Repository
public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Integer> {

    /**
     * Finds events by processing status.
     * 
     * @param status the processing status
     * @return list of events
     */
    List<NotificationEvent> findByProcessingStatus(ProcessingStatus status);

    /**
     * Finds an event by correlation ID.
     * 
     * @param correlationId the correlation ID
     * @return optional event
     */
    Optional<NotificationEvent> findByCorrelationId(String correlationId);

    /**
     * Finds events by source service.
     * 
     * @param sourceService the source service name
     * @return list of events
     */
    List<NotificationEvent> findBySourceService(String sourceService);
}

