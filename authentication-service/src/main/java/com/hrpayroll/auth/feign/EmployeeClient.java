package com.hrpayroll.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * OpenFeign client for Employee Service
 * Used to fetch employeeId by userId for JWT generation
 */
@FeignClient(name = "employee-service")
public interface EmployeeClient {

    @GetMapping("/api/employees/employee-id")
    Long getEmployeeIdByUserId(@RequestParam("userId") Long userId);
}

