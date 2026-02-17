package com.cosmacare.cosmacare_user_service.service;

import com.cosmacare.cosmacare_user_service.dto.RegisterRequest;
import com.cosmacare.cosmacare_user_service.dto.StoreWorker;
import com.cosmacare.cosmacare_user_service.dto.UserDTO;
import com.cosmacare.cosmacare_user_service.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    String registerUser(RegisterRequest req);
    Optional<UserDTO> getUserByUsername(String username);
    List<User> getAllUsers();

    List<StoreWorker> getActiveWorkers();

//    LoginResponse login(LoginRequest req);

    String deleteUser(String name);

}