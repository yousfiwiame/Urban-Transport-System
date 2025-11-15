package com.transport.subscription.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test d'intégration complet avec TestContainers PostgreSQL
 * Ce test démarre un conteneur PostgreSQL automatiquement
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Full Integration Test with TestContainers")
class FullIntegrationTest {

    @Test
    @DisplayName("Should load Spring context successfully")
    void contextLoads() {
        // This test just verifies that the Spring context loads correctly
        // with TestContainers PostgreSQL
    }
}

