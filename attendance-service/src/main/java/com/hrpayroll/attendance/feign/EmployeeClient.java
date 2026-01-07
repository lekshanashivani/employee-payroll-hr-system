package com.hrpayroll.attendance.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * OpenFeign client for Employee Service
 * Used to get employee role for leave approval logic
 */
@FeignClient(name = "employee-service")
public interface EmployeeClient {

    @GetMapping("/api/employees/{employeeId}/role")
    String getEmployeeRole(@PathVariable("employeeId") Long employeeId);
}

