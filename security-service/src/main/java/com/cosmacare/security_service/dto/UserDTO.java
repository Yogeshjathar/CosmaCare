package com.cosmacare.security_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
    private UUID userId;
    private String username;
    private String password;
    private String role;

    // Getters and setters
}