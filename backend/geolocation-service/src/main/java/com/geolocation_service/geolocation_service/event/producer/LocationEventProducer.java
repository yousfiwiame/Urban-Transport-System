package com.geolocation_service.geolocation_service.event.producer;

import com.geolocation_service.geolocation_service.event.BusArrivedAtStopEvent;
import com.geolocation_service.geolocation_service.event.BusDepartedFromStopEvent;
import com.geolocation_service.geolocation_service.event.BusLocationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Event producer for publishing location-related events to Kafka.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LocationEventProducer {

    private static final String LOCATION_UPDATED_TOPIC = "bus-location-updated-events";
    private static final String BUS_ARRIVED_TOPIC = "bus-arrived-events";
    private static final String BUS_DEPARTED_TOPIC = "bus-departed-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes a bus location updated event to Kafka.
     */
    public void publishLocationUpdated(BusLocationUpdatedEvent event) {
        log.info("Publishing bus location updated event for bus: {}", event.getBusId());
        kafkaTemplate.send(LOCATION_UPDATED_TOPIC, event.getBusId(), event);
    }

    /**
     * Publishes a bus arrived at stop event to Kafka.
     */
    public void publishBusArrived(BusArrivedAtStopEvent event) {
        log.info("Publishing bus arrived event: bus {} at stop {}", event.getBusId(), event.getStopName());
        kafkaTemplate.send(BUS_ARRIVED_TOPIC, event.getBusId(), event);
    }

    /**
     * Publishes a bus departed from stop event to Kafka.
     */
    public void publishBusDeparted(BusDepartedFromStopEvent event) {
        log.info("Publishing bus departed event: bus {} from stop {}", event.getBusId(), event.getStopName());
        kafkaTemplate.send(BUS_DEPARTED_TOPIC, event.getBusId(), event);
    }
}
