package com.cosmacare.reward_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // Define server URLs for local and production environments
    Server localServer = new Server()
            .url("http://localhost:8083")
            .description("Local environment");

    Server productionServer = new Server()
            .url("https://api.cosmacare.com/reward-service") // Example production URL
            .description("Production environment");

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("CosmaCare - Reward Service API")
                        .version("1.0")
                        .description("This service manages reward points for store workers in the CosmaCare platform. "
                                + "It supports adding, updating, and retrieving reward point details based on store activities.")
                        .contact(new Contact()
                                .name("CosmaCare Dev Team")
                                .email("support@cosmacare.com")
                                .url("https://www.cosmacare.com")))
                // Register environment servers
                .addServersItem(localServer)
                .addServersItem(productionServer)
                // Add JWT Bearer token security
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
