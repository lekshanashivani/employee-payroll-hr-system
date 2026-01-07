package com.hrpayroll.auth.dto;

public class LoginResponse {
    private String token;
    private String email;
    private String role;
    private Long userId;
    private Long employeeId; // May be null if employee profile not created yet

    public LoginResponse() {
    }

    public LoginResponse(String token, String email, String role, Long userId, Long employeeId) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.userId = userId;
        this.employeeId = employeeId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}

