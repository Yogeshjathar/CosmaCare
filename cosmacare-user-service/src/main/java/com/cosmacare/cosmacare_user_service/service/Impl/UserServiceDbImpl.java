package com.cosmacare.cosmacare_user_service.service.Impl;

import com.cosmacare.cosmacare_user_service.dto.RegisterRequest;
import com.cosmacare.cosmacare_user_service.dto.StoreWorker;
import com.cosmacare.cosmacare_user_service.dto.UserDTO;
import com.cosmacare.cosmacare_user_service.entity.Status;
import com.cosmacare.cosmacare_user_service.entity.User;
import com.cosmacare.cosmacare_user_service.repository.UserRepository;
import com.cosmacare.cosmacare_user_service.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceDbImpl implements UserService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final MeterRegistry meterRegistry;

    public UserServiceDbImpl(UserRepository repo, PasswordEncoder passwordEncoder, MeterRegistry meterRegistry){
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.meterRegistry = meterRegistry;
    }
    @Override
    public String registerUser(RegisterRequest req) {
        log.info("User registration attempt started for username: {} and email: {}", req.getUsername(), req.getEmail());

        // check if email exists
        if (repo.findByEmail(req.getEmail()).isPresent()) {
            log.warn("Registration failed - Email already exists: {}", req.getEmail());
            return "Please Try With Different Email, User With \"" + req.getEmail() + "\" already Registered";
        }

        // check if username exists
        if (repo.findByUserName(req.getUsername()).isPresent()) {
            log.warn("Registration failed - Username already exists: {}", req.getUsername());
            return "User with \"" + req.getUsername() + "\" Username already exists !!! Please Try with different Username";
        }

        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setUserName(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setRole(req.getRole());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        if(req.getStatus() != null){
            user.setStatus(req.getStatus());
        }else{
            user.setStatus(Status.ACTIVE);
        }
        repo.save(user);

        log.info("User registration successful for username: {} (email: {}, role: {})",
                req.getUsername(), req.getEmail(), req.getRole());

        return "Welcome " + req.getFirstName() + ", registration successfully !!!";
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByUsernameFallback")
    @Override
    public Optional<UserDTO> getUserByUsername(String username) {
        log.info("Fetching user details for username: {}", username);

        Optional<UserDTO> userDTO = repo.findByUserName(username)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUserName(),
                        user.getPassword(),
                        user.getRole().name()
                ));

        if (userDTO.isPresent()) {
            log.info("User found: {}", username);
        } else {
            log.warn("User not found: {}", username);
        }

        return userDTO;
    }

    // Fallback method
    public Optional<UserDTO> getUserByUsernameFallback(String username, Throwable ex) {
        log.error("Fallback triggered for getUserByUsername due to exception: {}", ex.getMessage());
        return Optional.of(new UserDTO(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                username,
                "N/A",
                "UNKNOWN_ROLE"
        ));
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Fetching all users from database...");

        List<User> users = repo.findAll();

        log.info("Total users fetched: {}", users.size());
        log.debug("User details: {}", users);

        return users;
    }

    @Override
    public List<StoreWorker> getActiveWorkers() {
        log.info("Fetching all store workers from database...");

        List<StoreWorker> workers = repo.findByStatus(Status.valueOf("ACTIVE"))
                .stream()
                .map(user -> new StoreWorker(
                        user.getUserName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getStatus()
                ))
                .toList();

        log.info("Total store workers fetched: {}", workers.size());
        log.debug("Store workers data: {}", workers);

        return workers;
    }

    @Override
    public String deleteUser(String name) {
        // Start timer
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            log.info("Attempting to delete user: {}", name);

            boolean exists = repo.existsByUserName(name);
            if (!exists) {
                log.warn("User '{}' not found. Deletion aborted.", name);
                meterRegistry.counter("user.delete.failure").increment();
                return "User not found!";
            }

            repo.deleteByUserName(name);
            log.info("User '{}' deleted successfully.", name);
            meterRegistry.counter("user.delete.success").increment();

            // Optional gauge for current users
            meterRegistry.gauge("users.count", repo.count());

            return "User deleted successfully !!!";
        } catch (Exception e) {
            log.error("Error deleting user '{}': {}", name, e.getMessage(), e);
            meterRegistry.counter("user.delete.failure").increment();
            throw e;
        } finally {
            sample.stop(meterRegistry.timer("user.delete.timer"));
            log.debug("Deletion process for user '{}' completed.", name);
        }
    }

}
