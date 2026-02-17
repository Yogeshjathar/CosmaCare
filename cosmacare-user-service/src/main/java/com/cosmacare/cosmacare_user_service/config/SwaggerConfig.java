package com.cosmacare.cosmacare_user_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        // Define server URLs for local and production environments
        Server localServer = new Server()
                .url("http://localhost:8081")
                .description("Local environment");

        Server productionServer = new Server()
                .url("https://api.cosmacare.com/user-service")
                .description("Production environment");

        return new OpenAPI()
                .info(new Info()
                        .title("CosmaCare - User Service API")
                        .version("1.0")
                        .description("This service handles user management for the CosmaCare platform, "
                                + "including registration, authentication, and role-based access control.")
                        .contact(new Contact()
                                .name("CosmaCare Dev Team")
                                .email("support@cosmacare.com")
                                .url("https://www.cosmacare.com")))
                .servers(List.of(localServer, productionServer))
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
