package com.cosmacare.cosmacare_user_service.dto;

import com.cosmacare.cosmacare_user_service.entity.Role;
import com.cosmacare.cosmacare_user_service.entity.Status;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private Role role;
    private Status status;

    // getter and setter
}
