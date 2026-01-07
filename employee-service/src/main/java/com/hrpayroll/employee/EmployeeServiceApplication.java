package com.hrpayroll.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Employee Service
 * 
 * Responsibilities:
 * - Employee lifecycle management
 * - Profile updates
 * - Designation assignment
 * - Soft deletion (deactivation)
 */
@SpringBootApplication
@EnableFeignClients
public class EmployeeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeServiceApplication.class, args);
    }
}

