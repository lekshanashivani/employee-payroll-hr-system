package com.hrpayroll.auth.controller;

import com.hrpayroll.auth.dto.CreateUserRequest;
import com.hrpayroll.auth.dto.LoginRequest;
import com.hrpayroll.auth.dto.LoginResponse;
import com.hrpayroll.auth.entity.Role;
import com.hrpayroll.auth.entity.User;
import com.hrpayroll.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * 
 * Exposes REST endpoints for:
 * - Login
 * - User creation (ADMIN only for HR accounts)
 * - Role updates (ADMIN only)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @RequestHeader("X-User-Id") Long createdByUserId,
            @RequestHeader("X-User-Role") String createdByRole) {
        try {
            User user = authService.createUser(request, createdByUserId, createdByRole);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<User> updateUserRole(
            @PathVariable Long userId,
            @RequestParam Role newRole,
            @RequestHeader("X-User-Id") Long updatedByUserId) {
        try {
            User user = authService.updateUserRole(userId, newRole, updatedByUserId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        try {
            authService.deactivateUser(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

