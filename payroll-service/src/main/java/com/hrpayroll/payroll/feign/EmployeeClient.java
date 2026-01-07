package com.hrpayroll.payroll.feign;

import com.hrpayroll.payroll.dto.DesignationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * OpenFeign client for Employee Service
 * 
 * Critical service - Payroll MUST fail if Employee service is unavailable
 */
@FeignClient(name = "employee-service")
public interface EmployeeClient {

    @GetMapping("/api/employees/{employeeId}/designation")
    DesignationDTO getDesignationByEmployeeId(@PathVariable("employeeId") Long employeeId);
}

