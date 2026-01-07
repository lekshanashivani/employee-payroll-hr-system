package com.hrpayroll.payroll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Payroll Service
 * 
 * Responsibilities:
 * - Salary calculation (leave-aware, NOT attendance-based)
 * - Payslip generation
 * - Bonus management
 * 
 * Payroll Formula:
 * Net Salary = Base Salary + Bonuses - Unpaid Leave Deduction - Tax
 * 
 * Payroll MUST take a salary SNAPSHOT.
 * Historical payslips must never change.
 */
@SpringBootApplication
@EnableFeignClients
public class PayrollServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayrollServiceApplication.class, args);
    }
}

