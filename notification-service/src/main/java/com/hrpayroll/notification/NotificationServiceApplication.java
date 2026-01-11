package com.hrpayroll.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Notification Service
 * 
 * Responsibilities:
 * - Send real emails via Gmail SMTP + App Password
 * - Store notification history
 * - HR announcements
 * 
 * Email Rules:
 * - Non-blocking
 * - One retry maximum
 * - No business logic
 */
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableFeignClients
@EnableAsync
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
