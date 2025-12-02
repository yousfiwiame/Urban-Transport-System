package com.transport.urbain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a schedule is delayed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDelayedEvent {
    private Long scheduleId;
    private Long routeId;
    private String routeName;
    private LocalDateTime originalDepartureTime;
    private LocalDateTime newDepartureTime;
    private Integer delayMinutes;
    private String delayReason;
    private LocalDateTime notifiedAt;
}
