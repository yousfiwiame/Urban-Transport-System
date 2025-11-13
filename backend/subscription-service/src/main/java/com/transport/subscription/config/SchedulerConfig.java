package com.transport.subscription.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    // Scheduler configuration
    // The actual scheduling is done via @Scheduled annotations in scheduler components
}

