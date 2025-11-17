package com.transport.subscription.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration CORS pour autoriser les requêtes depuis le front-end
 *
 * Placez ce fichier dans : src/main/java/com/transport/subscription/config/
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Autoriser les requêtes depuis le front-end (port 3000)
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",  // Port Vite (front-end)
                "http://localhost:5173",  // Port Vite alternatif
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5173"
        ));

        // Autoriser toutes les méthodes HTTP
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Autoriser tous les headers
        config.setAllowedHeaders(Arrays.asList("*"));

        // Autoriser les credentials (cookies, auth headers)
        config.setAllowCredentials(true);

        // Durée de cache pour les requêtes preflight (1 heure)
        config.setMaxAge(3600L);

        // Appliquer cette configuration à tous les endpoints
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

