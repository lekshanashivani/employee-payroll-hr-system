package com.hrpayroll.auth.feign;

import com.hrpayroll.auth.dto.AuditLogRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * OpenFeign client for Audit Log Service
 * Used for auditing role changes and user creation
 */
@FeignClient(name = "audit-log-service")
public interface AuditLogClient {

    @PostMapping("/api/audit-logs")
    void createAuditLog(@RequestBody AuditLogRequest request);
}

