package com.transport.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for the Notification Service microservice.
 * 
 * <p>This is a Spring Boot application that provides notification management
 * functionality as part of the Urban Transport System. It handles:
 * <ul>
 *   <li>Email notifications</li>
 *   <li>SMS notifications</li>
 *   <li>Push notifications</li>
 *   <li>Notification templates</li>
 *   <li>User notification preferences</li>
 *   <li>Event-driven notifications from other services</li>
 * </ul>
 * 
 * <p>Enabled features:
 * <ul>
 *   <li>Spring Boot auto-configuration</li>
 *   <li>Service discovery client for microservices integration</li>
 *   <li>JPA auditing for automatic timestamp tracking</li>
 * </ul>
 * 
 * <p>The application integrates with:
 * <ul>
 *   <li>Service Registry (Eureka) for service discovery</li>
 *   <li>Config Server for centralized configuration</li>
 *   <li>Kafka for consuming events from other services</li>
 *   <li>PostgreSQL database for notification data persistence</li>
 * </ul>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
public class NotificationServiceApplication {

    /**
     * Main entry point for the Notification Service application.
     * 
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}

