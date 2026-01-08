package com.hrpayroll.employee.dto;

import com.hrpayroll.employee.entity.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO for creating employee with user account in one request
 * 
 * Combines user creation (email, password, role) with employee profile data
 */
public class CreateEmployeeWithUserRequest {
    
    // User fields (for Authentication Service)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotNull(message = "Role is required")
    private String role; // ADMIN, HR, or EMPLOYEE
    
    // Employee fields
    @NotBlank(message = "Name is required")
    private String name;
    
    private String phoneNumber;
    
    private LocalDate dateOfBirth;
    
    private String address;
    
    @NotBlank(message = "Department is required")
    private String department;
    
    @NotNull(message = "Designation ID is required")
    private Long designationId;
    
    private EmployeeStatus status = EmployeeStatus.ACTIVE;
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public Long getDesignationId() {
        return designationId;
    }
    
    public void setDesignationId(Long designationId) {
        this.designationId = designationId;
    }
    
    public EmployeeStatus getStatus() {
        return status;
    }
    
    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }
}

