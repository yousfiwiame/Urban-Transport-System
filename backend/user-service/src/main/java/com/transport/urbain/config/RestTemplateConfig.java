package com.transport.urbain.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for RestTemplate to communicate with other microservices
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced // Enable Eureka service discovery
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
