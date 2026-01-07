package com.hrpayroll.payroll.feign;

import com.hrpayroll.payroll.dto.PayrollNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * OpenFeign client for Notification Service
 * 
 * Non-critical service - Payroll must NOT fail if Notification service is unavailable
 */
@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/api/notifications/payroll-generated")
    void sendPayrollGeneratedNotification(@RequestBody PayrollNotificationRequest request);
}

