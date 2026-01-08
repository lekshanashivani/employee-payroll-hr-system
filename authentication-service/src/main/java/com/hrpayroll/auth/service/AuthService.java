package com.hrpayroll.auth.service;

import com.hrpayroll.auth.dto.AuditLogRequest;
import com.hrpayroll.auth.dto.CreateUserRequest;
import com.hrpayroll.auth.dto.LoginRequest;
import com.hrpayroll.auth.dto.LoginResponse;
import com.hrpayroll.auth.entity.Role;
import com.hrpayroll.auth.entity.User;
import com.hrpayroll.auth.feign.AuditLogClient;
import com.hrpayroll.auth.feign.EmployeeClient;
import com.hrpayroll.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Service
 * 
 * Handles user authentication, login, and user creation.
 * Integrates with Employee Service to get employeeId for JWT.
 * Integrates with Audit Log Service for role change auditing.
 */
@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private AuditLogClient auditLogClient;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.getActive()) {
            throw new RuntimeException("User account is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

       
        Long employeeId = null;
        try {
            employeeId = employeeClient.getEmployeeIdByUserId(user.getId());
        } catch (Exception e) {
            // Employee profile may not exist yet, continue without employeeId
        }

        String token = jwtService.generateToken(user.getId(), user.getRole().name(), employeeId);

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getId(),
                employeeId
        );
    }

    /**
     * Create user account
     * Only ADMIN can create HR accounts
     */
    public User createUser(CreateUserRequest request, Long createdByUserId, String createdByRole) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Only ADMIN can create HR accounts
        if (request.getRole() == Role.HR && createdByRole != null && !createdByRole.equals("ADMIN")) {
            throw new RuntimeException("Only ADMIN can create HR accounts");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // Audit role creation
        try {
            Map<String, Object> oldValues = new HashMap<>();
            Map<String, Object> newValues = new HashMap<>();
            newValues.put("role", request.getRole().name());
            newValues.put("email", request.getEmail());

            AuditLogRequest auditRequest = new AuditLogRequest();
            auditRequest.setAction("USER_CREATED");
            auditRequest.setServiceName("Authentication Service");
            auditRequest.setPerformedBy(createdByUserId);
            auditRequest.setTargetId(savedUser.getId());
            auditRequest.setDescription("User account created with role: " + request.getRole().name());
            auditRequest.setOldValues(oldValues);
            auditRequest.setNewValues(newValues);
            auditLogClient.createAuditLog(auditRequest);
        } catch (Exception e) {
            // Non-blocking audit
        }

        return savedUser;
    }

    /**
     * Update user role
     * Only ADMIN can change roles
     */
    public User updateUserRole(Long userId, Role newRole, Long updatedByUserId, String updatedByRole) {
        // Authorization check: Only ADMIN can change roles
        if (updatedByRole == null || !updatedByRole.equals("ADMIN")) {
            throw new RuntimeException("Only ADMIN can change user roles");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent self-role-change (ADMIN cannot change their own role)
        if (userId.equals(updatedByUserId)) {
            throw new RuntimeException("Cannot change your own role");
        }

        // Prevent changing role to the same role
        if (user.getRole() == newRole) {
            throw new RuntimeException("User already has this role");
        }

        Role oldRole = user.getRole();
        user.setRole(newRole);
        User savedUser = userRepository.save(user);

        // Audit role change
        try {
            Map<String, Object> oldValues = new HashMap<>();
            oldValues.put("role", oldRole.name());

            Map<String, Object> newValues = new HashMap<>();
            newValues.put("role", newRole.name());

            AuditLogRequest auditRequest = new AuditLogRequest();
            auditRequest.setAction("ROLE_CHANGED");
            auditRequest.setServiceName("Authentication Service");
            auditRequest.setPerformedBy(updatedByUserId);
            auditRequest.setTargetId(userId);
            auditRequest.setDescription("User role changed from " + oldRole + " to " + newRole);
            auditRequest.setOldValues(oldValues);
            auditRequest.setNewValues(newValues);
            auditLogClient.createAuditLog(auditRequest);
        } catch (Exception e) {
            // Non-blocking audit
        }

        return savedUser;
    }

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(false);
        userRepository.save(user);
    }
}

