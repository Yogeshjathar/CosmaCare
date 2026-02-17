package com.cosmacare.cosmacare_user_service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue
    private UUID id;
    private String firstName;
    private String lastName;
    @Column(name = "username", nullable = false, unique = true)
    @JsonProperty("username")
    private String userName;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Status status;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;  // password stored as hash password for security purpose
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getter & Setter
}

