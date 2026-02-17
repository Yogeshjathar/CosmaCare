package com.cosmacare.cosmacare_user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityBypassConfig {

    @Bean
    public SecurityFilterChain bypassFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .securityMatcher("/actuator/**", "/swagger-ui/**") // only these endpoints
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
