package com.hrpayroll.attendance.service;

import com.hrpayroll.attendance.dto.AuditLogRequest;
import com.hrpayroll.attendance.dto.HrMeetingNotificationRequest;
import com.hrpayroll.attendance.entity.HrMeetingRequest;
import com.hrpayroll.attendance.entity.MeetingStatus;
import com.hrpayroll.attendance.feign.AuditLogClient;
import com.hrpayroll.attendance.feign.NotificationClient;
import com.hrpayroll.attendance.repository.HrMeetingRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HR Meeting Service
 * 
 * Handles HR meeting requests.
 * EMPLOYEE can request HR meeting
 * HR can approve or reject
 * Notification sent on decision
 * Audit only approval/rejection
 */
@Service
@Transactional
public class HrMeetingService {

    @Autowired
    private HrMeetingRequestRepository meetingRequestRepository;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private AuditLogClient auditLogClient;

    public HrMeetingRequest createMeetingRequest(HrMeetingRequest meetingRequest) {
        meetingRequest.setStatus(MeetingStatus.PENDING);
        return meetingRequestRepository.save(meetingRequest);
    }

    public HrMeetingRequest approveMeetingRequest(Long meetingRequestId, LocalDateTime scheduledDateTime,
            Long approvedByUserId) {
        HrMeetingRequest meetingRequest = meetingRequestRepository.findById(meetingRequestId)
                .orElseThrow(() -> new RuntimeException("Meeting request not found"));

        if (meetingRequest.getStatus() != MeetingStatus.PENDING) {
            throw new RuntimeException("Meeting request is not pending");
        }

        meetingRequest.setStatus(MeetingStatus.APPROVED);
        meetingRequest.setApprovedBy(approvedByUserId);
        meetingRequest.setApprovedAt(LocalDateTime.now());
        meetingRequest.setScheduledDateTime(scheduledDateTime);
        HrMeetingRequest saved = meetingRequestRepository.save(meetingRequest);

        // Send notification
        try {
            HrMeetingNotificationRequest request = new HrMeetingNotificationRequest();
            request.setEmployeeId(meetingRequest.getEmployeeId());
            request.setMeetingRequestId(meetingRequestId);
            request.setApproved(true);
            request.setScheduledDateTime(scheduledDateTime);
            notificationClient.sendHrMeetingNotification(request);
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
            newValues.put("scheduledDateTime", scheduledDateTime.toString());

            AuditLogRequest auditRequest = new AuditLogRequest();
            auditRequest.setAction("HR_MEETING_APPROVED");
            auditRequest.setServiceName("Attendance Service");
            auditRequest.setPerformedBy(approvedByUserId);
            auditRequest.setTargetId(meetingRequestId);
            auditRequest.setDescription("HR meeting request approved");
            auditRequest.setOldValues(oldValues);
            auditRequest.setNewValues(newValues);
            auditLogClient.createAuditLog(auditRequest);
        } catch (Exception e) {
            // Non-blocking audit
        }

        return saved;
    }

    public HrMeetingRequest rejectMeetingRequest(Long meetingRequestId, String rejectionReason, Long rejectedByUserId) {
        HrMeetingRequest meetingRequest = meetingRequestRepository.findById(meetingRequestId)
                .orElseThrow(() -> new RuntimeException("Meeting request not found"));

        if (meetingRequest.getStatus() != MeetingStatus.PENDING) {
            throw new RuntimeException("Meeting request is not pending");
        }

        meetingRequest.setStatus(MeetingStatus.REJECTED);
        meetingRequest.setApprovedBy(rejectedByUserId);
        meetingRequest.setApprovedAt(LocalDateTime.now());
        meetingRequest.setRejectionReason(rejectionReason);
        HrMeetingRequest saved = meetingRequestRepository.save(meetingRequest);

        // Send notification
        try {
            HrMeetingNotificationRequest request = new HrMeetingNotificationRequest();
            request.setEmployeeId(meetingRequest.getEmployeeId());
            request.setMeetingRequestId(meetingRequestId);
            request.setApproved(false);
            request.setScheduledDateTime(null);
            notificationClient.sendHrMeetingNotification(request);
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
            auditRequest.setAction("HR_MEETING_REJECTED");
            auditRequest.setServiceName("Attendance Service");
            auditRequest.setPerformedBy(rejectedByUserId);
            auditRequest.setTargetId(meetingRequestId);
            auditRequest.setDescription("HR meeting request rejected: " + rejectionReason);
            auditRequest.setOldValues(oldValues);
            auditRequest.setNewValues(newValues);
            auditLogClient.createAuditLog(auditRequest);
        } catch (Exception e) {
            // Non-blocking audit
        }

        return saved;
    }

    public List<HrMeetingRequest> getMeetingRequestsByEmployee(Long employeeId) {
        return meetingRequestRepository.findByEmployeeId(employeeId);
    }

    public List<HrMeetingRequest> getPendingMeetingRequests() {
        return meetingRequestRepository.findByStatus(MeetingStatus.PENDING);
    }

    public List<HrMeetingRequest> getScheduledMeetingRequests() {
        return meetingRequestRepository.findByStatus(MeetingStatus.APPROVED);
    }

    public HrMeetingRequest concludeMeetingRequest(Long meetingRequestId) {
        HrMeetingRequest meetingRequest = meetingRequestRepository.findById(meetingRequestId)
                .orElseThrow(() -> new RuntimeException("Meeting request not found"));

        if (meetingRequest.getStatus() != MeetingStatus.APPROVED) {
            throw new RuntimeException("Meeting is not in APPROVED (Scheduled) state");
        }

        meetingRequest.setStatus(MeetingStatus.CONCLUDED);
        return meetingRequestRepository.save(meetingRequest);
    }
}
