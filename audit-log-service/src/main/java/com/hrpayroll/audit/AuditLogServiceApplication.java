package com.hrpayroll.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Audit Log Service
 * 
 * Responsibilities:
 * - Centralized audit logging
 * - Compliance and traceability
 * 
 * Audit ONLY:
 * - Role changes
 * - Designation changes
 * - Payroll generation
 * - Leave approval/rejection
 * - Bonus grants
 * - Employee deactivation
 * - Announcements
 * 
 * Audit Rules:
 * - No login or read auditing
 * - Partial old/new JSON only
 * - Non-blocking
 */
@SpringBootApplication
public class AuditLogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuditLogServiceApplication.class, args);
    }
}

