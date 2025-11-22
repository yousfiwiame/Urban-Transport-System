package com.transport.urbain.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka consumer configuration for the Schedule Service.
 * <p>
 * This configuration class sets up Kafka consumers to receive messages from Kafka topics.
 * It configures deserializers for message keys (String) and values (JSON), enabling
 * the service to consume events from other microservices.
 * <p>
 * The configuration uses:
 * <ul>
 *     <li>String deserializer for message keys</li>
 *     <li>JSON deserializer for message values</li>
 *     <li>Bootstrap servers from application configuration</li>
 *     <li>Consumer group ID for proper message distribution</li>
 * </ul>
 */
@Configuration
public class KafkaConsumerConfig {

    /**
     * Kafka bootstrap servers address.
     * Injected from application configuration (e.g., application.yml).
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Consumer group ID for the schedule service.
     * This ensures that messages are properly distributed among service instances.
     */
    @Value("${spring.kafka.consumer.group-id:schedule-service-group}")
    private String groupId;

    /**
     * Creates and configures the Kafka consumer factory.
     * <p>
     * This factory is responsible for creating Kafka consumers with the following settings:
     * <ul>
     *     <li>Bootstrap servers configuration</li>
     *     <li>String deserializer for message keys</li>
     *     <li>JSON deserializer for message values</li>
     *     <li>Consumer group ID for message distribution</li>
     *     <li>Auto offset reset to earliest (read from beginning if no offset exists)</li>
     *     <li>Trusted packages for JSON deserialization</li>
     * </ul>
     *
     * @return configured ConsumerFactory for String keys and Object values
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Configure bootstrap servers (Kafka broker addresses)
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        // Configure consumer group ID
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        
        // Configure key deserializer (String)
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        // Configure value deserializer (JSON)
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        
        // Auto offset reset: read from beginning if no offset exists
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        // Trust all packages for JSON deserialization (for receiving events from other services)
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        
        // Disable type information headers (must match producer configuration)
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Object.class);
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Creates a Kafka listener container factory for concurrent message processing.
     * <p>
     * This factory is used by @KafkaListener annotations to process messages concurrently.
     * It allows multiple threads to process messages from Kafka topics simultaneously.
     * <p>
     * The factory is configured with:
     * <ul>
     *     <li>The consumer factory for creating consumers</li>
     *     <li>Concurrent message processing support</li>
     * </ul>
     *
     * @return ConcurrentKafkaListenerContainerFactory configured with the consumer factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // Enable auto-acknowledgment (messages are acknowledged automatically after processing)
        // For manual acknowledgment, set this to MANUAL and use Acknowledgment parameter in listeners
        return factory;
    }
}

