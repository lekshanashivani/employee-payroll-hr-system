package com.hrpayroll.attendance.controller;

import com.hrpayroll.attendance.dto.MarkAttendanceRequest;
import com.hrpayroll.attendance.entity.Attendance;
import com.hrpayroll.attendance.entity.HrMeetingRequest;
import com.hrpayroll.attendance.entity.LeaveRequest;
import com.hrpayroll.attendance.service.AttendanceService;
import com.hrpayroll.attendance.service.HrMeetingService;
import com.hrpayroll.attendance.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Attendance Controller
 * 
 * Exposes REST endpoints for attendance, leave, and HR meeting management.
 */
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private HrMeetingService hrMeetingService;

    // Attendance endpoints
    @PostMapping("/mark")
    public ResponseEntity<Attendance> markAttendance(
            @RequestBody MarkAttendanceRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-Employee-Id", required = false) String requestEmployeeIdStr) {

        // Security Check: Only allow marking for self or if Admin/HR
        boolean isAdminOrHr = "ADMIN".equals(userRole) || "HR".equals(userRole);
        boolean isSelf = requestEmployeeIdStr != null && !requestEmployeeIdStr.isEmpty()
                && Long.parseLong(requestEmployeeIdStr) == request.getEmployeeId();

        if (!isAdminOrHr && !isSelf) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Attendance attendance = attendanceService.markAttendance(request.getEmployeeId(), request.getDate());
            return ResponseEntity.status(HttpStatus.CREATED).body(attendance);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Attendance>> getAttendanceByEmployeeAndDateRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Attendance> attendance = attendanceService.getAttendanceByEmployeeAndDateRange(employeeId, startDate,
                endDate);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Attendance>> getAllAttendance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        if (!"ADMIN".equals(userRole) && !"HR".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Attendance> attendance = attendanceService.getAllAttendance(startDate, endDate);
        return ResponseEntity.ok(attendance);
    }

    // Leave endpoints
    @PostMapping("/leave-requests")
    public ResponseEntity<?> createLeaveRequest(@RequestBody LeaveRequest leaveRequest) {
        try {
            LeaveRequest created = leaveService.createLeaveRequest(leaveRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/leave-requests/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequest>> getLeaveRequestsByEmployee(@PathVariable Long employeeId) {
        List<LeaveRequest> leaveRequests = leaveService.getLeaveRequestsByEmployee(employeeId);
        return ResponseEntity.ok(leaveRequests);
    }

    @GetMapping("/leave-requests/pending")
    public ResponseEntity<List<LeaveRequest>> getPendingLeaveRequests() {
        List<LeaveRequest> leaveRequests = leaveService.getPendingLeaveRequests();
        return ResponseEntity.ok(leaveRequests);
    }

    @PutMapping("/leave-requests/{id}/approve")
    public ResponseEntity<LeaveRequest> approveLeaveRequest(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long approvedByUserId,
            @RequestHeader("X-User-Role") String approvedByRole) {

        if (!"ADMIN".equals(approvedByRole) && !"HR".equals(approvedByRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            LeaveRequest approved = leaveService.approveLeaveRequest(id, approvedByUserId, approvedByRole);
            return ResponseEntity.ok(approved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/leave-requests/{id}/reject")
    public ResponseEntity<LeaveRequest> rejectLeaveRequest(
            @PathVariable Long id,
            @RequestParam String rejectionReason,
            @RequestHeader("X-User-Id") Long rejectedByUserId,
            @RequestHeader("X-User-Role") String rejectedByRole) {

        if (!"ADMIN".equals(rejectedByRole) && !"HR".equals(rejectedByRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            LeaveRequest rejected = leaveService.rejectLeaveRequest(id, rejectionReason, rejectedByUserId,
                    rejectedByRole);
            return ResponseEntity.ok(rejected);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/leave-requests/unpaid")
    public ResponseEntity<List<LeaveRequest>> getApprovedUnpaidLeaves(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LeaveRequest> leaves = leaveService.getApprovedUnpaidLeaves(employeeId, startDate, endDate);
        return ResponseEntity.ok(leaves);
    }

    // HR Meeting endpoints
    @PostMapping("/hr-meetings")
    public ResponseEntity<HrMeetingRequest> createMeetingRequest(@RequestBody HrMeetingRequest meetingRequest) {
        try {
            HrMeetingRequest created = hrMeetingService.createMeetingRequest(meetingRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/hr-meetings/employee/{employeeId}")
    public ResponseEntity<List<HrMeetingRequest>> getMeetingRequestsByEmployee(@PathVariable Long employeeId) {
        List<HrMeetingRequest> meetings = hrMeetingService.getMeetingRequestsByEmployee(employeeId);
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/hr-meetings/pending")
    public ResponseEntity<List<HrMeetingRequest>> getPendingMeetingRequests() {
        List<HrMeetingRequest> meetings = hrMeetingService.getPendingMeetingRequests();
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/hr-meetings/scheduled")
    public ResponseEntity<List<HrMeetingRequest>> getScheduledMeetingRequests() {
        List<HrMeetingRequest> meetings = hrMeetingService.getScheduledMeetingRequests();
        return ResponseEntity.ok(meetings);
    }

    @PutMapping("/hr-meetings/{id}/approve")
    public ResponseEntity<HrMeetingRequest> approveMeetingRequest(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledDateTime,
            @RequestHeader("X-User-Id") Long approvedByUserId) {
        try {
            HrMeetingRequest approved = hrMeetingService.approveMeetingRequest(id, scheduledDateTime, approvedByUserId);
            return ResponseEntity.ok(approved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/hr-meetings/{id}/reject")
    public ResponseEntity<HrMeetingRequest> rejectMeetingRequest(
            @PathVariable Long id,
            @RequestParam String rejectionReason,
            @RequestHeader("X-User-Id") Long rejectedByUserId) {
        try {
            HrMeetingRequest rejected = hrMeetingService.rejectMeetingRequest(id, rejectionReason, rejectedByUserId);
            return ResponseEntity.ok(rejected);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
