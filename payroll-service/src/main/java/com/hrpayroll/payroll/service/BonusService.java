package com.hrpayroll.payroll.service;

import com.hrpayroll.payroll.dto.AuditLogRequest;
import com.hrpayroll.payroll.entity.Bonus;
import com.hrpayroll.payroll.feign.AuditLogClient;
import com.hrpayroll.payroll.repository.BonusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bonus Service
 * 
 * Handles bonus management.
 * Bonuses are granted by HR and audited.
 */
@Service
@Transactional
public class BonusService {

    @Autowired
    private BonusRepository bonusRepository;

    @Autowired
    private AuditLogClient auditLogClient;

    public Bonus grantBonus(Bonus bonus, Long grantedByUserId) {
        // Validate dates
        if (bonus.getStartDate().isAfter(bonus.getEndDate())) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        bonus.setGrantedBy(grantedByUserId);
        bonus.setGrantedAt(LocalDateTime.now());
        Bonus saved = bonusRepository.save(bonus);

        // Audit bonus grant
        try {
            Map<String, Object> newValues = new HashMap<>();
            newValues.put("employeeId", bonus.getEmployeeId());
            newValues.put("amount", bonus.getAmount().toString());
            newValues.put("startDate", bonus.getStartDate().toString());
            newValues.put("endDate", bonus.getEndDate().toString());

            AuditLogRequest auditRequest = new AuditLogRequest();
            auditRequest.setAction("BONUS_GRANTED");
            auditRequest.setServiceName("Payroll Service");
            auditRequest.setPerformedBy(grantedByUserId);
            auditRequest.setTargetId(saved.getId());
            auditRequest.setDescription("Bonus granted: " + bonus.getAmount());
            auditRequest.setOldValues(null);
            auditRequest.setNewValues(newValues);
            auditLogClient.createAuditLog(auditRequest);
        } catch (Exception e) {
            // Non-blocking audit
        }

        return saved;
    }

    public List<Bonus> getBonusesByEmployee(Long employeeId) {
        return bonusRepository.findByEmployeeId(employeeId);
    }

    public Bonus getBonusById(Long id) {
        return bonusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bonus not found"));
    }
}

