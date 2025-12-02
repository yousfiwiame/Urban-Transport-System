package com.transport.urbain.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;

/**
 * Configuration for Eureka Client authentication.
 * <p>
 * This configuration adds HTTP Basic Authentication to all RestTemplates,
 * including the one used by the Eureka client, allowing the config-server
 * to authenticate with the secured Eureka server (service-registry).
 * <p>
 * The credentials are read from environment variables or application properties:
 * <ul>
 *     <li>EUREKA_USERNAME (default: eureka)</li>
 *     <li>EUREKA_PASSWORD (default: eureka123)</li>
 * </ul>
 */
@Configuration
public class EurekaClientConfig {

    @Value("${EUREKA_USERNAME:eureka}")
    private String eurekaUsername;

    @Value("${EUREKA_PASSWORD:eureka123}")
    private String eurekaPassword;

    /**
     * Configures a RestTemplateCustomizer that adds HTTP Basic Authentication
     * to all RestTemplates, including the one used by Eureka client.
     * <p>
     * This ensures that all HTTP requests to the Eureka server include
     * authentication headers. Bean overriding is enabled in application properties
     * to allow this to override the default LoadBalancer RestTemplateCustomizer.
     *
     * @return RestTemplateCustomizer that adds authentication interceptors
     */
    @Bean
    public RestTemplateCustomizer restTemplateCustomizer() {
        return restTemplate -> {
            restTemplate.getInterceptors().add(
                new BasicAuthenticationInterceptor(eurekaUsername, eurekaPassword)
            );
        };
    }
}

