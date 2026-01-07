package com.hrpayroll.auth.entity;

/**
 * System roles
 * 
 * ADMIN: Full system access, creates HR accounts
 * HR: Employee management, leave approvals, payroll operations
 * EMPLOYEE: Self-service, leave requests, attendance tracking
 */
public enum Role {
    ADMIN,
    HR,
    EMPLOYEE
}

