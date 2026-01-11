package com.hrpayroll.audit.controller;

import com.hrpayroll.audit.entity.AuditLog;
import com.hrpayroll.audit.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Audit Log Controller
 * 
 * Exposes REST endpoints for audit log management.
 */
@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @PostMapping
    public ResponseEntity<AuditLog> createAuditLog(@RequestBody AuditLogRequest request) {
        try {
            AuditLog auditLog = auditLogService.createAuditLog(
                    request.getAction(),
                    request.getServiceName(),
                    request.getPerformedBy(),
                    request.getTargetId(),
                    request.getDescription(),
                    request.getOldValues(),
                    request.getNewValues());
            return ResponseEntity.status(HttpStatus.CREATED).body(auditLog);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getAuditLogById(@PathVariable("id") Long id) {
        try {
            AuditLog auditLog = auditLogService.getAuditLogById(id);
            return ResponseEntity.ok(auditLog);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/performed-by/{performedBy}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByPerformedBy(@PathVariable("performedBy") Long performedBy) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByPerformedBy(performedBy);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/target/{targetId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByTargetId(@PathVariable("targetId") Long targetId) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByTargetId(targetId);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByAction(@PathVariable("action") String action) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByAction(action);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/service/{serviceName}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByServiceName(@PathVariable("serviceName") String serviceName) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByServiceName(serviceName);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        List<AuditLog> auditLogs = auditLogService.getAllAuditLogs();
        return ResponseEntity.ok(auditLogs);
    }

    // DTO
    public static class AuditLogRequest {
        private String action;
        private String serviceName;
        private Long performedBy;
        private Long targetId;
        private String description;
        private Map<String, Object> oldValues;
        private Map<String, Object> newValues;

        // Getters and Setters
        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public Long getPerformedBy() {
            return performedBy;
        }

        public void setPerformedBy(Long performedBy) {
            this.performedBy = performedBy;
        }

        public Long getTargetId() {
            return targetId;
        }

        public void setTargetId(Long targetId) {
            this.targetId = targetId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Map<String, Object> getOldValues() {
            return oldValues;
        }

        public void setOldValues(Map<String, Object> oldValues) {
            this.oldValues = oldValues;
        }

        public Map<String, Object> getNewValues() {
            return newValues;
        }

        public void setNewValues(Map<String, Object> newValues) {
            this.newValues = newValues;
        }
    }
}
