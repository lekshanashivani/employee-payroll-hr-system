package com.hrpayroll.payroll.dto;

/**
 * DTO for payroll generated notification
 */
public class PayrollNotificationRequest {
    private Long employeeId;
    private Long payslipId;
    private String payPeriod;

    // Getters and Setters
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

