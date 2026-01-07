package com.hrpayroll.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Authentication Service
 * 
 * Responsibilities:
 * - User login and authentication
 * - JWT token generation
 * - Role assignment
 * - ADMIN creates HR accounts
 * 
 * This service does NOT handle:
 * - Employee profile data
 * - Payroll, attendance, or leave logic
 */
@SpringBootApplication
@EnableFeignClients
public class AuthenticationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationServiceApplication.class, args);
    }
}

