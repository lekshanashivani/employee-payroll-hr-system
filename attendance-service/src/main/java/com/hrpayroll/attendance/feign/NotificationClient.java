package com.hrpayroll.attendance.feign;

import com.hrpayroll.attendance.dto.HrMeetingNotificationRequest;
import com.hrpayroll.attendance.dto.LeaveNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * OpenFeign client for Notification Service
 */
@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/api/notifications/leave-approval")
    void sendLeaveApprovalNotification(@RequestBody LeaveNotificationRequest request);

    @PostMapping("/api/notifications/hr-meeting")
    void sendHrMeetingNotification(@RequestBody HrMeetingNotificationRequest request);
}

