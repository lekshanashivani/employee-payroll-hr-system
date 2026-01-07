package com.hrpayroll.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Attendance Service
 * 
 * Responsibilities:
 * - Daily attendance tracking
 * - Leave management (PAID/UNPAID)
 * - HR meeting requests
 */
@SpringBootApplication
@EnableFeignClients
public class AttendanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceServiceApplication.class, args);
    }
}

