package com.cosmacare.repair_service.config;

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
            .url("http://localhost:8082")
            .description("Local environment");

    Server productionServer = new Server()
            .url("https://api.cosmacare.com/repair-service") // Example production URL
            .description("Production environment");
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("CosmaCare - Repair Service API")
                        .version("1.0")
                        .description("This service manages repair requests raised by store workers in the CosmaCare platform. "
                                + "It supports creating, updating, viewing, and tracking repair requests.")
                        .contact(new Contact()
                                .name("CosmaCare Dev Team")
                                .email("support@cosmacare.com")
                                .url("https://www.cosmacare.com")))
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
