package com.hrpayroll.attendance.feign;

import com.hrpayroll.attendance.dto.AuditLogRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * OpenFeign client for Audit Log Service
 */
@FeignClient(name = "audit-log-service")
public interface AuditLogClient {

    @PostMapping("/api/audit-logs")
    void createAuditLog(@RequestBody AuditLogRequest request);
}

