package com.transport.urbain.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test-specific Kafka configuration that provides a mock KafkaTemplate
 * for integration tests.
 * <p>
 * This configuration is only active when the "test" profile is enabled.
 * It provides a no-op KafkaTemplate to prevent errors when event producers
 * try to publish events during tests.
 */
@TestConfiguration
@Profile("test")
public class TestKafkaConfig {

    /**
     * Provides a mock KafkaTemplate bean for testing.
     * <p>
     * This mock template does nothing when send() is called, allowing tests
     * to run without a real Kafka broker.
     *
     * @return a mocked KafkaTemplate instance
     */
    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate<String, Object> mockTemplate = mock(KafkaTemplate.class);
        when(mockTemplate.send(anyString(), any())).thenReturn(null);
        when(mockTemplate.send(anyString(), anyString(), any())).thenReturn(null);
        return mockTemplate;
    }
}
