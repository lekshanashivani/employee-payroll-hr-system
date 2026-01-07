package com.hrpayroll.notification.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * OpenFeign client for Employee Service
 * Used to get employee email and filter by audience
 */
@FeignClient(name = "employee-service")
public interface EmployeeClient {

    @GetMapping("/api/employees/{employeeId}/email")
    String getEmployeeEmail(@PathVariable("employeeId") Long employeeId);

    @GetMapping("/api/employees/audience/{audience}")
    List<Long> getEmployeeIdsByAudience(@PathVariable("audience") String audience);
}

