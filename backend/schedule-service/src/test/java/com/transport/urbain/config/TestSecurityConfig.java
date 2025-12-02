package com.transport.urbain.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test-specific security configuration that disables authentication and authorization
 * for integration tests.
 * <p>
 * This configuration is only active when the "test" profile is enabled.
 * It allows all requests without authentication to simplify integration testing.
 */
@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false)
@Profile("test")
public class TestSecurityConfig {

    /**
     * Configures a permissive security filter chain for testing.
     * <p>
     * All security features are disabled:
     * <ul>
     *     <li>CSRF protection is disabled</li>
     *     <li>All requests are permitted without authentication</li>
     *     <li>Method-level security (@PreAuthorize) is disabled</li>
     * </ul>
     *
     * @param http HttpSecurity object to configure
     * @return configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
