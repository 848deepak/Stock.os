package com.inventory.controller;

import com.inventory.dto.LoginRequestDTO;
import com.inventory.dto.LoginResponseDTO;
import com.inventory.dto.RefreshTokenRequestDTO;
import com.inventory.dto.RegisterRequestDTO;
import com.inventory.model.Role;
import com.inventory.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO request) {
        LoginResponseDTO response = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequestDTO request) {
        authService.registerUser(request.getUsername(), request.getEmail(), request.getPassword(), Role.RoleType.STAFF);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.getOrDefault("email", "");
        Map<String, String> response = new HashMap<>();

        if (email == null || email.isBlank()) {
            response.put("message", "Email is required");
            return ResponseEntity.badRequest().body(response);
        }

        // Placeholder flow: keeps API contract stable until email service is wired.
        response.put("message", "If the email exists, a reset link will be sent shortly.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Inventory Management System API is running");
        return ResponseEntity.ok(response);
    }
}
