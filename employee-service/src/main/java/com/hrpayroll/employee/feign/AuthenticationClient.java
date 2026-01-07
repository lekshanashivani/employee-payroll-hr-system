package com.hrpayroll.employee.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * OpenFeign client for Authentication Service
 * Used to deactivate user accounts when employee is deactivated
 */
@FeignClient(name = "authentication-service")
public interface AuthenticationClient {

    @PutMapping("/api/auth/users/{userId}/deactivate")
    void deactivateUser(@PathVariable("userId") Long userId);
}

