package com.cosmacare.security_service.controller;

import com.cosmacare.security_service.Security.JwtUtil;
import com.cosmacare.security_service.dto.LoginRequest;
import com.cosmacare.security_service.dto.LoginResponse;
import com.cosmacare.security_service.dto.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    /*@PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getAuthorities().iterator().next().getAuthority());

        String token = jwtUtil.doGenerateToken(claims, user.getUsername());

        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), claims.get("role").toString()));
    }*/

    @PostMapping("/login")
    @Timed(value = "auth.login.time", description = "Time taken to authenticate a user")
    @Counted(value = "auth.login.count", description = "Number of login attempts")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        String role = user.getAuthorities().iterator().next().getAuthority();
        UUID userId = user.getId();

        String token = jwtUtil.generateToken(user.getUsername(), role, userId);

        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), role));
    }

    @PostMapping("/validate")
    @Timed(value = "auth.token.validate.time", description = "Time taken to validate a JWT token")
    @Counted(value = "auth.token.validate.count", description = "Number of token validation requests")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = jwtUtil.validateToken(token);
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("username", claims.getSubject());
            response.put("role", claims.get("role"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

}