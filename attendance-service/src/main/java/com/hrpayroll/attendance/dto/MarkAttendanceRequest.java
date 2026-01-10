package com.hrpayroll.attendance.dto;

import java.time.LocalDate;

public class MarkAttendanceRequest {
    private Long employeeId;
    private LocalDate date;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
