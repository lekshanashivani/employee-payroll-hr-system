package com.hrpayroll.employee.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * OpenFeign client for Authentication Service
 * Used to create user accounts and deactivate them
 */
@FeignClient(name = "authentication-service")
public interface AuthenticationClient {

    @PostMapping("/api/auth/users")
    UserResponse createUser(
            @RequestBody CreateUserRequestDTO request,
            @RequestHeader("X-User-Id") Long createdByUserId,
            @RequestHeader("X-User-Role") String createdByRole);

    @PutMapping("/api/auth/users/{userId}/deactivate")
    void deactivateUser(@PathVariable("userId") Long userId);

    // DTOs
    class CreateUserRequestDTO {
        private String email;
        private String password;
        private String role;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    class UserResponse {
        private Long id;
        private String email;
        private String role; // Role enum serialized as string
        private Boolean active;
        // Other fields (createdAt, updatedAt) are ignored

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
    }
}

