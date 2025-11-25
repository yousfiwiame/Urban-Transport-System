package com.transport.urbain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security configuration for the API Gateway.
 * <p>
 * This configuration disables Spring Security's default authentication
 * and allows all requests to pass through. JWT authentication is handled
 * by the custom JwtAuthenticationFilter at the Gateway route level.
 * <p>
 * Configuration includes:
 * <ul>
 *     <li>Disabled CSRF protection (REST API best practice for stateless APIs)</li>
 *     <li>All requests are permitted without Spring Security authentication</li>
 *     <li>JWT validation is done by JwtAuthenticationFilter in GatewayConfig</li>
 * </ul>
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Configures the security web filter chain for reactive WebFlux.
     * <p>
     * This configuration:
     * <ul>
     *     <li>Disables CSRF protection for stateless REST API</li>
     *     <li>Permits all requests without Spring Security authentication</li>
     *     <li>Relies on JwtAuthenticationFilter for JWT token validation</li>
     * </ul>
     *
     * @param http the ServerHttpSecurity to configure
     * @return the configured SecurityWebFilterChain
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .build();
    }
}

