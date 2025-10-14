package com.cosmacare.security_service.client;

import com.cosmacare.security_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cosmacare-user-service")
public interface UserServiceClient {

    @GetMapping("/api/user/getUserByUsername/{username}")
    UserDTO getUserByUsername(@PathVariable("username") String username);
}
