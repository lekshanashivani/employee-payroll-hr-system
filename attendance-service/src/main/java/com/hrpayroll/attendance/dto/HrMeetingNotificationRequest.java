package com.hrpayroll.attendance.dto;

import java.time.LocalDateTime;

/**
 * DTO for HR meeting approval/rejection notification
 */
public class HrMeetingNotificationRequest {
    private Long employeeId;
    private Long meetingRequestId;
    private Boolean approved;
    private LocalDateTime scheduledDateTime;

    // Getters and Setters
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

