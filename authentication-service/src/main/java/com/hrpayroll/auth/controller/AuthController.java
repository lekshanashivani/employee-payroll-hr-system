package com.hrpayroll.auth.controller;

import com.hrpayroll.auth.dto.CreateUserRequest;
import com.hrpayroll.auth.dto.LoginRequest;
import com.hrpayroll.auth.dto.LoginResponse;
import com.hrpayroll.auth.entity.Role;
import com.hrpayroll.auth.entity.User;
import com.hrpayroll.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Authentication and user management APIs")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "User login", description = "Authenticate user and get JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
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

    @Operation(summary = "Create user", description = "Create a new user (ADMIN only). Only ADMIN can create HR accounts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request - Email already exists or only ADMIN can create HR accounts")
    })
    @PostMapping("/users")
    public ResponseEntity<User> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @Parameter(description = "ID of the user creating the account", required = true, example = "1")
            @RequestHeader("X-User-Id") Long createdByUserId,
            @Parameter(description = "Role of the user creating the account (must be ADMIN for HR accounts)", required = true, example = "ADMIN")
            @RequestHeader("X-User-Role") String createdByRole) {
        try {
            User user = authService.createUser(request, createdByUserId, createdByRole);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Update user role", description = "Update user role (ADMIN only). Cannot change your own role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request - Only ADMIN can change roles, cannot change own role, or user already has this role"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<User> updateUserRole(
            @Parameter(description = "ID of the user whose role is being changed", required = true, example = "2")
            @PathVariable("userId") Long userId,
            @Parameter(description = "New role to assign (ADMIN, HR, or EMPLOYEE)", required = true, example = "HR")
            @RequestParam(name = "newRole", required = true) Role newRole,
            @Parameter(description = "ID of the ADMIN user making the change", required = true, example = "1")
            @RequestHeader("X-User-Id") Long updatedByUserId,
            @Parameter(description = "Role of the user making the change (must be ADMIN)", required = true, example = "ADMIN")
            @RequestHeader("X-User-Role") String updatedByRole) {
        try {
            User user = authService.updateUserRole(userId, newRole, updatedByUserId, updatedByRole);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Deactivate user", description = "Deactivate a user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(
            @Parameter(description = "ID of the user to deactivate", required = true, example = "2")
            @PathVariable Long userId) {
        try {
            authService.deactivateUser(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

