package com.hrpayroll.attendance.service;

import com.hrpayroll.attendance.dto.AuditLogRequest;
import com.hrpayroll.attendance.dto.LeaveNotificationRequest;
import com.hrpayroll.attendance.entity.LeaveRequest;
import com.hrpayroll.attendance.entity.LeaveStatus;
import com.hrpayroll.attendance.entity.LeaveType;
import com.hrpayroll.attendance.feign.AuditLogClient;
import com.hrpayroll.attendance.feign.EmployeeClient;
import com.hrpayroll.attendance.feign.NotificationClient;
import com.hrpayroll.attendance.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Leave Service
 * 
 * Handles leave requests and approvals.
 * EMPLOYEE leave → HR approval
 * HR leave → ADMIN approval
 * Self-approval forbidden
 */
@Service
@Transactional
public class LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private AuditLogClient auditLogClient;

    public LeaveRequest createLeaveRequest(LeaveRequest leaveRequest) {
        // Validate dates
        if (leaveRequest.getStartDate().isAfter(leaveRequest.getEndDate())) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        if (leaveRequest.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot request leave for past dates");
        }

        leaveRequest.setStatus(LeaveStatus.PENDING);
        return leaveRequestRepository.save(leaveRequest);
    }

    /**
     * Approve leave request
     * EMPLOYEE leave → HR approval
     * HR leave → ADMIN approval
     * Self-approval forbidden
     */
    public LeaveRequest approveLeaveRequest(Long leaveRequestId, Long approvedByUserId, String approvedByRole) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Leave request is not pending");
        }

        // Check self-approval
        if (leaveRequest.getEmployeeId().equals(approvedByUserId)) {
            throw new RuntimeException("Cannot approve own leave request");
        }

        // Get employee role to determine who can approve
        String employeeRole = employeeClient.getEmployeeRole(leaveRequest.getEmployeeId());

        // EMPLOYEE leave → HR approval
        // HR leave → ADMIN approval
        if (employeeRole.equals("EMPLOYEE") && !approvedByRole.equals("HR")) {
            throw new RuntimeException("Only HR can approve employee leave requests");
        }
        if (employeeRole.equals("HR") && !approvedByRole.equals("ADMIN")) {
            throw new RuntimeException("Only ADMIN can approve HR leave requests");
        }

        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setApprovedBy(approvedByUserId);
        leaveRequest.setApprovedAt(LocalDateTime.now());
        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);

        // Send notification
        try {
            LeaveNotificationRequest request = new LeaveNotificationRequest();
            request.setEmployeeId(leaveRequest.getEmployeeId());
            request.setLeaveRequestId(leaveRequest.getId());
            request.setApproved(true);
            request.setRejectionReason(null);
            notificationClient.sendLeaveApprovalNotification(request);
        } catch (Exception e) {
            // Non-blocking notification
        }

        // Audit approval
        try {
            Map<String, Object> oldValues = new HashMap<>();
            oldValues.put("status", "PENDING");

            Map<String, Object> newValues = new HashMap<>();
            newValues.put("status", "APPROVED");
            newValues.put("approvedBy", approvedByUserId);

            AuditLogRequest auditRequest = new AuditLogRequest();
            auditRequest.setAction("LEAVE_APPROVED");
            auditRequest.setServiceName("Attendance Service");
            auditRequest.setPerformedBy(approvedByUserId);
            auditRequest.setTargetId(leaveRequestId);
            auditRequest.setDescription("Leave request approved");
            auditRequest.setOldValues(oldValues);
            auditRequest.setNewValues(newValues);
            auditLogClient.createAuditLog(auditRequest);
        } catch (Exception e) {
            // Non-blocking audit
        }

        return saved;
    }

    public LeaveRequest rejectLeaveRequest(Long leaveRequestId, String rejectionReason, Long rejectedByUserId,
            String rejectedByRole) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Leave request is not pending");
        }

        // Check self-approval
        if (leaveRequest.getEmployeeId().equals(rejectedByUserId)) {
            throw new RuntimeException("Cannot reject own leave request");
        }

        // Get employee role to determine who can reject
        String employeeRole = employeeClient.getEmployeeRole(leaveRequest.getEmployeeId());

        // EMPLOYEE leave → HR rejection
        // HR leave → ADMIN rejection
        if (employeeRole.equals("EMPLOYEE") && !rejectedByRole.equals("HR")) {
            throw new RuntimeException("Only HR can reject employee leave requests");
        }
        if (employeeRole.equals("HR") && !rejectedByRole.equals("ADMIN")) {
            throw new RuntimeException("Only ADMIN can reject HR leave requests");
        }

        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setApprovedBy(rejectedByUserId);
        leaveRequest.setApprovedAt(LocalDateTime.now());
        leaveRequest.setRejectionReason(rejectionReason);
        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);

        // Send notification
        try {
            LeaveNotificationRequest request = new LeaveNotificationRequest();
            request.setEmployeeId(leaveRequest.getEmployeeId());
            request.setLeaveRequestId(leaveRequest.getId());
            request.setApproved(false);
            request.setRejectionReason(rejectionReason);
            notificationClient.sendLeaveApprovalNotification(request);
        } catch (Exception e) {
            // Non-blocking notification
        }

        // Audit rejection
        try {
            Map<String, Object> oldValues = new HashMap<>();
            oldValues.put("status", "PENDING");

            Map<String, Object> newValues = new HashMap<>();
            newValues.put("status", "REJECTED");
            newValues.put("rejectedBy", rejectedByUserId);
            newValues.put("rejectionReason", rejectionReason);

            AuditLogRequest auditRequest = new AuditLogRequest();
            auditRequest.setAction("LEAVE_REJECTED");
            auditRequest.setServiceName("Attendance Service");
            auditRequest.setPerformedBy(rejectedByUserId);
            auditRequest.setTargetId(leaveRequestId);
            auditRequest.setDescription("Leave request rejected: " + rejectionReason);
            auditRequest.setOldValues(oldValues);
            auditRequest.setNewValues(newValues);
            auditLogClient.createAuditLog(auditRequest);
        } catch (Exception e) {
            // Non-blocking audit
        }

        return saved;
    }

    public List<LeaveRequest> getLeaveRequestsByEmployee(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    public List<LeaveRequest> getPendingLeaveRequests() {
        return leaveRequestRepository.findByStatus(LeaveStatus.PENDING);
    }

    public List<LeaveRequest> getApprovedUnpaidLeaves(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return leaveRequestRepository.findApprovedUnpaidLeaves(employeeId, LeaveType.UNPAID, startDate, endDate);
    }
}
