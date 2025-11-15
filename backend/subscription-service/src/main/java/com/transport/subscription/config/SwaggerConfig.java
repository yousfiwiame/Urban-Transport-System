package com.transport.subscription.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8085}")
    private String serverPort;

    @Value("${spring.application.name:subscription-service}")
    private String applicationName;

    @Value("${server.address:localhost}")
    private String hostname;

    @Bean
    public OpenAPI subscriptionServiceOpenAPI() {
        List<Server> servers = new ArrayList<>();
        
        // Local development server
        servers.add(new Server()
                .url("http://localhost:" + serverPort)
                .description("Local Development Server"));
        
        // Docker/internal network server
        servers.add(new Server()
                .url("http://" + hostname + ":" + serverPort)
                .description("Internal Network Server"));
        
        // Production server (if configured)
        String prodUrl = System.getenv("API_GATEWAY_URL");
        if (prodUrl != null && !prodUrl.isEmpty()) {
            servers.add(new Server()
                    .url(prodUrl)
                    .description("Production Server (via API Gateway)"));
        }

        return new OpenAPI()
                .info(new Info()
                        .title("Subscription Service API")
                        .description("Subscription Management Microservice for Urban Transport System. " +
                                "This service handles subscription plans, user subscriptions, payments, " +
                                "automatic renewals, and QR code generation.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Urban Transport Team")
                                .email("support@urbantransport.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(servers);
    }
}

