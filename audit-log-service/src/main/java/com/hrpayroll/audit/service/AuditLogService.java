package com.hrpayroll.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrpayroll.audit.entity.AuditLog;
import com.hrpayroll.audit.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Audit Log Service
 * 
 * Handles centralized audit logging.
 * Non-blocking operation.
 */
@Service
@Transactional
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public AuditLog createAuditLog(String action, String serviceName, Long performedBy,
                                   Long targetId, String description,
                                   Map<String, Object> oldValues, Map<String, Object> newValues) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setServiceName(serviceName);
        auditLog.setPerformedBy(performedBy);
        auditLog.setTargetId(targetId);
        auditLog.setDescription(description);

        // Convert maps to JSON strings
        try {
            if (oldValues != null && !oldValues.isEmpty()) {
                auditLog.setOldValues(objectMapper.writeValueAsString(oldValues));
            }
            if (newValues != null && !newValues.isEmpty()) {
                auditLog.setNewValues(objectMapper.writeValueAsString(newValues));
            }
        } catch (JsonProcessingException e) {
            // Log error but continue
        }

        return auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getAuditLogsByPerformedBy(Long performedBy) {
        return auditLogRepository.findByPerformedBy(performedBy);
    }

    public List<AuditLog> getAuditLogsByTargetId(Long targetId) {
        return auditLogRepository.findByTargetId(targetId);
    }

    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    public List<AuditLog> getAuditLogsByServiceName(String serviceName) {
        return auditLogRepository.findByServiceName(serviceName);
    }

    public AuditLog getAuditLogById(Long id) {
        return auditLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit log not found"));
    }
}

