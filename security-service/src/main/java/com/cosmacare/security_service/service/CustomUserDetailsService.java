package com.cosmacare.security_service.service;

import com.cosmacare.security_service.dto.UserDTO;
import com.cosmacare.security_service.dto.UserPrincipal;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final RestTemplate restTemplate;

    public CustomUserDetailsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackUser")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String url = "http://cosmacare-user-service/api/user/getUserByUsername/" + username;

        ResponseEntity<UserDTO> response = restTemplate.getForEntity(url, UserDTO.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            UserDTO user = response.getBody();

/*            return User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole()) // assumes role is a simple string like "ADMIN"
                    .build();*/
            return new UserPrincipal(
                    user.getUserId(),
                    user.getUsername(),
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
            );
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }

    public UserDetails fallbackUser(String username, Throwable ex) {
        // Log the fallback trigger
        log.error("Fallback triggered for username: " + username + " due to: " + ex.getMessage());
        // Return a guest user or throw a custom exception
        return new UserPrincipal(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                username,
                "",
                List.of(new SimpleGrantedAuthority("ROLE_GUEST"))
        );
    }
}