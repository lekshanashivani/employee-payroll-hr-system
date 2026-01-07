package com.hrpayroll.payroll.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * OpenFeign client for Attendance Service
 * 
 * Critical service - Payroll MUST fail if Attendance service is unavailable
 */
@FeignClient(name = "attendance-service")
public interface AttendanceClient {

    @GetMapping("/api/attendance/leave-requests/unpaid")
    List<UnpaidLeaveDTO> getApprovedUnpaidLeaves(
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate);

    class UnpaidLeaveDTO {
        private Long id;
        private LocalDate startDate;
        private LocalDate endDate;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    }
}

