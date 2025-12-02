package com.transport.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration for the Notification Service.
 * Enables JPA auditing for automatic timestamp tracking.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
