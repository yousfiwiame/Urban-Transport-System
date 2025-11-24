package com.transport.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration for enabling scheduled tasks.
 * 
 * <p>Enables Spring's scheduling functionality for processing pending
 * notifications and retries.
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
    // Configuration for scheduled tasks
}

