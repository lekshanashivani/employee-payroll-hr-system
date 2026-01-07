package com.hrpayroll.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway
 * 
 * Responsibilities:
 * - JWT validation
 * - Routing to microservices
 * - Context propagation via headers:
 *   - X-User-Id
 *   - X-Employee-Id
 *   - X-User-Role
 * 
 * Forbidden:
 * - Business logic
 * - Database access
 * - Authorization decisions
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

