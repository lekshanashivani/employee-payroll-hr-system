package com.hrpayroll.payroll.service;

import com.hrpayroll.payroll.dto.AuditLogRequest;
import com.hrpayroll.payroll.dto.DesignationDTO;
import com.hrpayroll.payroll.dto.PayrollNotificationRequest;
import com.hrpayroll.payroll.entity.Bonus;
import com.hrpayroll.payroll.entity.Payslip;
import com.hrpayroll.payroll.feign.AttendanceClient;
import com.hrpayroll.payroll.feign.AuditLogClient;
import com.hrpayroll.payroll.feign.EmployeeClient;
import com.hrpayroll.payroll.feign.NotificationClient;
import com.hrpayroll.payroll.repository.BonusRepository;
import com.hrpayroll.payroll.repository.PayslipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Payroll Service
 * 
 * Handles payroll calculation and payslip generation.
 * 
 * Payroll Formula:
 * Net Salary = Base Salary + Bonuses - Unpaid Leave Deduction - Tax
 * 
 * Rules:
 * - Payroll MUST take a salary SNAPSHOT
 * - Historical payslips must never change
 * - ONLY UNPAID leave affects salary
 * - Payroll MUST fail if Employee or Attendance service is unavailable
 * - Payroll must NOT fail if Notification or Audit service is unavailable
 */
@Service
@Transactional
public class PayrollService {

    @Autowired
    private PayslipRepository payslipRepository;

    @Autowired
    private BonusRepository bonusRepository;

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private AttendanceClient attendanceClient;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private AuditLogClient auditLogClient;

    /**
     * Generate payslip for an employee for a specific pay period
     * 
     * This method:
     * 1. Gets designation from Employee Service (CRITICAL - must fail if unavailable)
     * 2. Gets unpaid leaves from Attendance Service (CRITICAL - must fail if unavailable)
     * 3. Gets active bonuses for the period
     * 4. Calculates salary components
     * 5. Creates payslip snapshot
     * 6. Sends notification (non-blocking)
     * 7. Creates audit log (non-blocking)
     */
    public Payslip generatePayslip(Long employeeId, YearMonth payPeriod, Long generatedByUserId) {
        // Check if payslip already exists
        if (payslipRepository.findByEmployeeIdAndPayPeriod(employeeId, payPeriod).isPresent()) {
            throw new RuntimeException("Payslip already exists for this pay period");
        }

        // CRITICAL: Get designation from Employee Service
        // This MUST fail if Employee service is unavailable
        DesignationDTO designation = employeeClient.getDesignationByEmployeeId(employeeId);
        if (designation == null) {
            throw new RuntimeException("Failed to retrieve employee designation. Employee service unavailable.");
        }

        // Calculate period dates
        LocalDate periodStart = payPeriod.atDay(1);
        LocalDate periodEnd = payPeriod.atEndOfMonth();

        // CRITICAL: Get unpaid leaves from Attendance Service
        // This MUST fail if Attendance service is unavailable
        List<AttendanceClient.UnpaidLeaveDTO> unpaidLeaves;
        try {
            unpaidLeaves = attendanceClient.getApprovedUnpaidLeaves(employeeId, periodStart, periodEnd);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve unpaid leaves. Attendance service unavailable.", e);
        }

        // Get active bonuses for the period
        List<Bonus> bonuses = bonusRepository.findActiveBonusesForPeriod(employeeId, periodStart, periodEnd);

        // Calculate salary components
        BigDecimal baseSalary = designation.getBaseSalary();
        BigDecimal totalBonuses = bonuses.stream()
                .map(Bonus::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate unpaid leave deduction
        int unpaidLeaveDays = calculateUnpaidLeaveDays(unpaidLeaves, periodStart, periodEnd);
        BigDecimal dailySalary = baseSalary.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
        BigDecimal unpaidLeaveDeduction = dailySalary.multiply(BigDecimal.valueOf(unpaidLeaveDays));

        // Calculate taxable amount (base salary + bonuses - unpaid leave deduction)
        BigDecimal taxableAmount = baseSalary.add(totalBonuses).subtract(unpaidLeaveDeduction);
        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }

        // Calculate tax
        BigDecimal taxAmount = taxableAmount.multiply(designation.getTaxPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Calculate net salary
        BigDecimal netSalary = taxableAmount.subtract(taxAmount);

        // Create payslip snapshot
        Payslip payslip = new Payslip();
        payslip.setEmployeeId(employeeId);
        payslip.setPayPeriod(payPeriod);
        payslip.setBaseSalary(baseSalary); // Snapshot
        payslip.setTaxPercentage(designation.getTaxPercentage()); // Snapshot
        payslip.setTotalBonuses(totalBonuses);
        payslip.setUnpaidLeaveDeduction(unpaidLeaveDeduction);
        payslip.setUnpaidLeaveDays(unpaidLeaveDays);
        payslip.setTaxAmount(taxAmount);
        payslip.setNetSalary(netSalary);
        payslip.setGeneratedBy(generatedByUserId);

        Payslip saved = payslipRepository.save(payslip);

        // NON-CRITICAL: Send notification (non-blocking)
        try {
            PayrollNotificationRequest request = new PayrollNotificationRequest();
            request.setEmployeeId(employeeId);
            request.setPayslipId(saved.getId());
            request.setPayPeriod(payPeriod.toString());
            notificationClient.sendPayrollGeneratedNotification(request);
        } catch (Exception e) {
            // Log but don't fail
        }

        // NON-CRITICAL: Create audit log (non-blocking)
        try {
            Map<String, Object> newValues = new HashMap<>();
            newValues.put("employeeId", employeeId);
            newValues.put("payPeriod", payPeriod.toString());
            newValues.put("netSalary", netSalary.toString());

            AuditLogRequest auditRequest = new AuditLogRequest();
            auditRequest.setAction("PAYROLL_GENERATED");
            auditRequest.setServiceName("Payroll Service");
            auditRequest.setPerformedBy(generatedByUserId);
            auditRequest.setTargetId(saved.getId());
            auditRequest.setDescription("Payslip generated for pay period: " + payPeriod);
            auditRequest.setOldValues(null);
            auditRequest.setNewValues(newValues);
            auditLogClient.createAuditLog(auditRequest);
        } catch (Exception e) {
            // Log but don't fail
        }

        return saved;
    }

    /**
     * Calculate total unpaid leave days within the pay period
     */
    private int calculateUnpaidLeaveDays(List<AttendanceClient.UnpaidLeaveDTO> unpaidLeaves, 
                                       LocalDate periodStart, LocalDate periodEnd) {
        int totalDays = 0;
        for (AttendanceClient.UnpaidLeaveDTO leave : unpaidLeaves) {
            LocalDate leaveStart = leave.getStartDate().isBefore(periodStart) ? periodStart : leave.getStartDate();
            LocalDate leaveEnd = leave.getEndDate().isAfter(periodEnd) ? periodEnd : leave.getEndDate();
            
            if (!leaveStart.isAfter(leaveEnd)) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(leaveStart, leaveEnd) + 1;
                totalDays += (int) days;
            }
        }
        return totalDays;
    }

    public Payslip getPayslipById(Long id) {
        return payslipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payslip not found"));
    }

    public List<Payslip> getPayslipsByEmployee(Long employeeId) {
        return payslipRepository.findByEmployeeId(employeeId);
    }

    public List<Payslip> getPayslipsByPayPeriod(YearMonth payPeriod) {
        return payslipRepository.findByPayPeriod(payPeriod);
    }
}

