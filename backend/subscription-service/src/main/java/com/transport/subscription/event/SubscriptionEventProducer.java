package com.transport.subscription.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.subscription-created:subscription-created}")
    private String subscriptionCreatedTopic;

    @Value("${kafka.topic.subscription-renewed:subscription-renewed}")
    private String subscriptionRenewedTopic;

    @Value("${kafka.topic.subscription-cancelled:subscription-cancelled}")
    private String subscriptionCancelledTopic;

    @Value("${kafka.topic.payment-processed:payment-processed}")
    private String paymentProcessedTopic;

    public void publishSubscriptionCreatedEvent(UUID subscriptionId, UUID userId, UUID planId) {
        try {
            SubscriptionCreatedEvent event = SubscriptionCreatedEvent.builder()
                    .subscriptionId(subscriptionId)
                    .userId(userId)
                    .planId(planId)
                    .timestamp(java.time.OffsetDateTime.now())
                    .build();
            kafkaTemplate.send(subscriptionCreatedTopic, subscriptionId.toString(), event);
            log.info("Published subscription created event: {}", subscriptionId);
        } catch (Exception e) {
            log.error("Failed to publish subscription created event: {}", e.getMessage(), e);
        }
    }

    public void publishSubscriptionRenewedEvent(UUID subscriptionId, UUID userId) {
        try {
            SubscriptionRenewedEvent event = SubscriptionRenewedEvent.builder()
                    .subscriptionId(subscriptionId)
                    .userId(userId)
                    .timestamp(java.time.OffsetDateTime.now())
                    .build();
            kafkaTemplate.send(subscriptionRenewedTopic, subscriptionId.toString(), event);
            log.info("Published subscription renewed event: {}", subscriptionId);
        } catch (Exception e) {
            log.error("Failed to publish subscription renewed event: {}", e.getMessage(), e);
        }
    }

    public void publishSubscriptionCancelledEvent(UUID subscriptionId, UUID userId, String reason) {
        try {
            SubscriptionCancelledEvent event = SubscriptionCancelledEvent.builder()
                    .subscriptionId(subscriptionId)
                    .userId(userId)
                    .reason(reason)
                    .timestamp(java.time.OffsetDateTime.now())
                    .build();
            kafkaTemplate.send(subscriptionCancelledTopic, subscriptionId.toString(), event);
            log.info("Published subscription cancelled event: {}", subscriptionId);
        } catch (Exception e) {
            log.error("Failed to publish subscription cancelled event: {}", e.getMessage(), e);
        }
    }

    public void publishPaymentProcessedEvent(UUID paymentId, UUID subscriptionId, String status) {
        try {
            PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                    .paymentId(paymentId)
                    .subscriptionId(subscriptionId)
                    .status(status)
                    .timestamp(java.time.OffsetDateTime.now())
                    .build();
            kafkaTemplate.send(paymentProcessedTopic, paymentId.toString(), event);
            log.info("Published payment processed event: {}", paymentId);
        } catch (Exception e) {
            log.error("Failed to publish payment processed event: {}", e.getMessage(), e);
        }
    }
}

