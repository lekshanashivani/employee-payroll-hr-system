package com.hrpayroll.payroll.feign;

import com.hrpayroll.payroll.dto.AuditLogRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * OpenFeign client for Audit Log Service
 * 
 * Non-critical service - Payroll must NOT fail if Audit Log service is unavailable
 */
@FeignClient(name = "audit-log-service")
public interface AuditLogClient {

    @PostMapping("/api/audit-logs")
    void createAuditLog(@RequestBody AuditLogRequest request);
}

