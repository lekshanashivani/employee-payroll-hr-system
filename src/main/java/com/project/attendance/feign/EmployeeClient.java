package com.project.attendance.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "employee-service")
public interface EmployeeClient {

    @GetMapping("/employees/test")
    String testEmployee();
}
