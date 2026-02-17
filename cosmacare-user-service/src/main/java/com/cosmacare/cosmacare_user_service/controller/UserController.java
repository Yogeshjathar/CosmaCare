package com.cosmacare.cosmacare_user_service.controller;

import com.cosmacare.cosmacare_user_service.dto.RegisterRequest;
import com.cosmacare.cosmacare_user_service.dto.StoreWorker;
import com.cosmacare.cosmacare_user_service.dto.UserDTO;
import com.cosmacare.cosmacare_user_service.entity.User;
import com.cosmacare.cosmacare_user_service.service.UserService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
//import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    @Timed(value = "user.register.timer", description = "Time taken to register a user")
    @Counted(value = "user.register.count", description = "Number of user registrations")
    public String register(@RequestBody RegisterRequest req) {
        return service.registerUser(req);
    }

    @GetMapping
    @Timed(value = "user.getAllUsers.timer", description = "Time taken to fetch all users")
    @Counted(value = "user.getAllUsers.count", description = "Number of times getAllUsers is called")
    public List<User> getUsers(){
        return service.getAllUsers();
    }

    @GetMapping("/getUserByUsername/{userName}")
    @Timed(value = "user.getUserByUsername.timer", description = "Time taken to fetch user by username")
    @Counted(value = "user.getUserByUsername.count", description = "Number of times getUserByUsername is called")
    public Optional<UserDTO> getUserByUsername(@PathVariable String userName){
        return service.getUserByUsername(userName);
    }

    @GetMapping("/getStoreWorkers")
    @Timed(value = "user.getWorkers.timer", description = "Time taken to fetch store workers")
    @Counted(value = "user.getWorkers.count", description = "Number of times getWorkers is called")
    public List<StoreWorker> getWorkers(){
        return service.getActiveWorkers();
    }

    @DeleteMapping("/delete/{name}")
    @Timed(value = "user.deleteUser.timer", description = "Time taken to call deleteUser API")
    @Counted(value = "user.deleteUser.count", description = "Number of times deleteUser API is called")
    public String deleteUserByUserName(@PathVariable String name) {
        return service.deleteUser(name);
    }

}

