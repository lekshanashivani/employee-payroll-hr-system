package com.hrpayroll.notification.controller;

import com.hrpayroll.notification.entity.Announcement;
import com.hrpayroll.notification.entity.Notification;
import com.hrpayroll.notification.service.AnnouncementService;
import com.hrpayroll.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Notification Controller
 * 
 * Exposes REST endpoints for notifications and announcements.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AnnouncementService announcementService;

    @PostMapping("/payroll-generated")
    public ResponseEntity<Void> sendPayrollGeneratedNotification(
            @RequestBody PayrollNotificationRequest request) {
        try {
            notificationService.createNotification(
                    request.getEmployeeId(),
                    "Payslip Generated",
                    "Your payslip for period " + request.getPayPeriod() + " has been generated.",
                    com.hrpayroll.notification.entity.NotificationType.PAYROLL_GENERATED);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/leave-approval")
    public ResponseEntity<Void> sendLeaveApprovalNotification(
            @RequestBody LeaveNotificationRequest request) {
        try {
            String subject = request.getApproved() ? "Leave Request Approved" : "Leave Request Rejected";
            String body = request.getApproved()
                    ? "Your leave request has been approved."
                    : "Your leave request has been rejected. Reason: " + request.getRejectionReason();

            notificationService.createNotification(
                    request.getEmployeeId(),
                    subject,
                    body,
                    request.getApproved()
                            ? com.hrpayroll.notification.entity.NotificationType.LEAVE_APPROVED
                            : com.hrpayroll.notification.entity.NotificationType.LEAVE_REJECTED);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/hr-meeting")
    public ResponseEntity<Void> sendHrMeetingNotification(
            @RequestBody HrMeetingNotificationRequest request) {
        try {
            String subject = request.getApproved() ? "HR Meeting Approved" : "HR Meeting Rejected";
            String body = request.getApproved()
                    ? "Your HR meeting request has been approved. Scheduled time: " + request.getScheduledDateTime()
                    : "Your HR meeting request has been rejected.";

            notificationService.createNotification(
                    request.getEmployeeId(),
                    subject,
                    body,
                    request.getApproved()
                            ? com.hrpayroll.notification.entity.NotificationType.HR_MEETING_APPROVED
                            : com.hrpayroll.notification.entity.NotificationType.HR_MEETING_REJECTED);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Notification>> getNotificationsByEmployee(@PathVariable Long employeeId) {
        List<Notification> notifications = notificationService.getNotificationsByEmployee(employeeId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        try {
            Notification notification = notificationService.getNotificationById(id);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Announcement endpoints
    @PostMapping("/announcements")
    public ResponseEntity<Announcement> createAnnouncement(
            @RequestBody Announcement announcement,
            @RequestHeader("X-User-Id") Long createdByUserId) {
        try {
            Announcement created = announcementService.createAnnouncement(announcement, createdByUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/announcements")
    public ResponseEntity<List<Announcement>> getActiveAnnouncements() {
        List<Announcement> announcements = announcementService.getActiveAnnouncements();
        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/announcements/{id}")
    public ResponseEntity<Announcement> getAnnouncementById(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.getAnnouncementById(id);
            return ResponseEntity.ok(announcement);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/announcements/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        try {
            announcementService.deleteAnnouncement(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DTOs
    public static class PayrollNotificationRequest {
        private Long employeeId;
        private Long payslipId;
        private String payPeriod;

        public Long getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(Long employeeId) {
            this.employeeId = employeeId;
        }

        public Long getPayslipId() {
            return payslipId;
        }

        public void setPayslipId(Long payslipId) {
            this.payslipId = payslipId;
        }

        public String getPayPeriod() {
            return payPeriod;
        }

        public void setPayPeriod(String payPeriod) {
            this.payPeriod = payPeriod;
        }
    }

    public static class LeaveNotificationRequest {
        private Long employeeId;
        private Long leaveRequestId;
        private Boolean approved;
        private String rejectionReason;

        public Long getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(Long employeeId) {
            this.employeeId = employeeId;
        }

        public Long getLeaveRequestId() {
            return leaveRequestId;
        }

        public void setLeaveRequestId(Long leaveRequestId) {
            this.leaveRequestId = leaveRequestId;
        }

        public Boolean getApproved() {
            return approved;
        }

        public void setApproved(Boolean approved) {
            this.approved = approved;
        }

        public String getRejectionReason() {
            return rejectionReason;
        }

        public void setRejectionReason(String rejectionReason) {
            this.rejectionReason = rejectionReason;
        }
    }

    public static class HrMeetingNotificationRequest {
        private Long employeeId;
        private Long meetingRequestId;
        private Boolean approved;
        private LocalDateTime scheduledDateTime;

        public Long getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(Long employeeId) {
            this.employeeId = employeeId;
        }

        public Long getMeetingRequestId() {
            return meetingRequestId;
        }

        public void setMeetingRequestId(Long meetingRequestId) {
            this.meetingRequestId = meetingRequestId;
        }

        public Boolean getApproved() {
            return approved;
        }

        public void setApproved(Boolean approved) {
            this.approved = approved;
        }

        public LocalDateTime getScheduledDateTime() {
            return scheduledDateTime;
        }

        public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
            this.scheduledDateTime = scheduledDateTime;
        }
    }
}
