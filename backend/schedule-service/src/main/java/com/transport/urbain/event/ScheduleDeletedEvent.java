package com.transport.urbain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a schedule is deleted/cancelled.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDeletedEvent {
    private Long scheduleId;
    private Long routeId;
    private String routeName;
    private LocalDateTime scheduledDepartureTime;
    private String cancellationReason;
    private LocalDateTime deletedAt;
}
