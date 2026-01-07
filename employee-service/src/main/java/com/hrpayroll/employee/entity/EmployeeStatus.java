package com.hrpayroll.employee.entity;

/**
 * Employee Status
 * 
 * ACTIVE: Employee is currently active
 * RESIGNED: Employee has resigned (soft delete)
 * TERMINATED: Employee has been terminated (soft delete)
 */
public enum EmployeeStatus {
    ACTIVE,
    RESIGNED,
    TERMINATED
}

