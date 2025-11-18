package com.transport.subscription.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class SubscriptionEventProducer {

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.subscription-created:subscription-created}")
    private String subscriptionCreatedTopic;

    @Value("${kafka.topic.subscription-renewed:subscription-renewed}")
    private String subscriptionRenewedTopic;

    @Value("${kafka.topic.subscription-cancelled:subscription-cancelled}")
    private String subscriptionCancelledTopic;

    @Value("${kafka.topic.payment-processed:payment-processed}")
    private String paymentProcessedTopic;

    public void publishSubscriptionCreatedEvent(Integer subscriptionId, Integer userId, Integer planId) {
        if (kafkaTemplate == null) {
            log.debug("Kafka not configured, skipping event publication");
            return;
        }
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

    public void publishSubscriptionRenewedEvent(Integer subscriptionId, Integer userId) {
        if (kafkaTemplate == null) {
            log.debug("Kafka not configured, skipping event publication");
            return;
        }
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

    public void publishSubscriptionCancelledEvent(Integer subscriptionId, Integer userId, String reason) {
        if (kafkaTemplate == null) {
            log.debug("Kafka not configured, skipping event publication");
            return;
        }
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

    public void publishPaymentProcessedEvent(Integer paymentId, Integer subscriptionId, String status) {
        if (kafkaTemplate == null) {
            log.debug("Kafka not configured, skipping event publication");
            return;
        }
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

