package com.hrpayroll.employee.service;

import com.hrpayroll.employee.dto.AuditLogRequest;
import com.hrpayroll.employee.dto.EmployeeDTO;
import com.hrpayroll.employee.entity.Designation;
import com.hrpayroll.employee.entity.Employee;
import com.hrpayroll.employee.entity.EmployeeStatus;
import com.hrpayroll.employee.feign.AuditLogClient;
import com.hrpayroll.employee.feign.AuthenticationClient;
import com.hrpayroll.employee.repository.DesignationRepository;
import com.hrpayroll.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Employee Service
 * 
 * Handles employee lifecycle, profile updates, and soft deletion.
 * HR can update: name, department, designation, status
 * Identity fields (userId, dateOfBirth) are immutable.
 */
@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private AuditLogClient auditLogClient;

    @Autowired
    private AuthenticationClient authenticationClient;

    /**
     * Create employee with user account in one operation
     * Creates user in Authentication Service first, then employee profile
     * 
     * Authorization:
     * - HR and ADMIN can create employees (role = EMPLOYEE)
     * - Only ADMIN can create HR accounts (role = HR)
     */
    public Employee createEmployee(
            com.hrpayroll.employee.dto.CreateEmployeeWithUserRequest request,
            Long createdByUserId,
            String createdByRole) {

        // Authorization check
        if (createdByRole == null) {
            throw new RuntimeException("User role is required");
        }

        // Only ADMIN can create HR accounts
        if ("HR".equalsIgnoreCase(request.getRole()) && !"ADMIN".equalsIgnoreCase(createdByRole)) {
            throw new RuntimeException("Only ADMIN can create HR accounts");
        }

        // HR and ADMIN can create employees
        if ("EMPLOYEE".equalsIgnoreCase(request.getRole()) &&
                !"ADMIN".equalsIgnoreCase(createdByRole) && !"HR".equalsIgnoreCase(createdByRole)) {
            throw new RuntimeException("Only HR and ADMIN can create employee accounts");
        }

        // Step 1: Create user in Authentication Service
        AuthenticationClient.CreateUserRequestDTO userRequest = new AuthenticationClient.CreateUserRequestDTO();
        userRequest.setEmail(request.getEmail());
        userRequest.setPassword(request.getPassword());
        userRequest.setRole(request.getRole());

        AuthenticationClient.UserResponse userResponse;
        try {
            userResponse = authenticationClient.createUser(userRequest, createdByUserId, createdByRole);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user account: " + e.getMessage());
        }

        if (userResponse == null || userResponse.getId() == null) {
            throw new RuntimeException("User creation failed - no user ID returned");
        }

        // Step 2: Create employee profile with the userId from created user
        Employee employee = new Employee();
        employee.setUserId(userResponse.getId());
        employee.setName(request.getName());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setAddress(request.getAddress());
        employee.setDepartment(request.getDepartment());
        employee.setStatus(request.getStatus() != null ? request.getStatus() : EmployeeStatus.ACTIVE);

        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new RuntimeException("Designation not found"));
        employee.setDesignation(designation);

        Employee savedEmployee = employeeRepository.save(employee);

        // Audit employee creation
        try {
            Map<String, Object> newValues = new HashMap<>();
            newValues.put("name", savedEmployee.getName());
            newValues.put("designationId", savedEmployee.getDesignation().getId());
            newValues.put("department", savedEmployee.getDepartment());
            newValues.put("status", savedEmployee.getStatus());

            AuditLogRequest auditRequest = new AuditLogRequest();
            auditRequest.setAction("EMPLOYEE_CREATED");
            auditRequest.setServiceName("Employee Service");
            auditRequest.setPerformedBy(createdByUserId);
            auditRequest.setTargetId(savedEmployee.getId());
            auditRequest.setDescription("New employee profile created");
            auditRequest.setNewValues(newValues);
            auditLogClient.createAuditLog(auditRequest);
        } catch (Exception e) {
            // Non-blocking audit
        }

        return savedEmployee;
    }

    /**
     * Update employee profile
     * HR can update: name, department, designation, status
     * Identity fields are immutable
     */
    public Employee updateEmployee(Long id, EmployeeDTO employeeDTO, Long updatedByUserId, String updatedByRole) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Store old values for audit
        Long oldDesignationId = employee.getDesignation().getId();

        // Update allowed fields
        if (employeeDTO.getName() != null) {
            employee.setName(employeeDTO.getName());
        }
        if (employeeDTO.getDepartment() != null) {
            employee.setDepartment(employeeDTO.getDepartment());
        }
        if (employeeDTO.getDesignationId() != null) {
            Designation newDesignation = designationRepository.findById(employeeDTO.getDesignationId())
                    .orElseThrow(() -> new RuntimeException("Designation not found"));
            employee.setDesignation(newDesignation);
        }
        if (employeeDTO.getStatus() != null) {
            employee.setStatus(employeeDTO.getStatus());
        }
        if (employeeDTO.getPhoneNumber() != null) {
            employee.setPhoneNumber(employeeDTO.getPhoneNumber());
        }
        if (employeeDTO.getAddress() != null) {
            employee.setAddress(employeeDTO.getAddress());
        }

        Employee savedEmployee = employeeRepository.save(employee);

        // Audit designation change
        if (employeeDTO.getDesignationId() != null && !oldDesignationId.equals(employeeDTO.getDesignationId())) {
            try {
                Map<String, Object> oldValues = new HashMap<>();
                oldValues.put("designationId", oldDesignationId);

                Map<String, Object> newValues = new HashMap<>();
                newValues.put("designationId", employeeDTO.getDesignationId());

                AuditLogRequest auditRequest = new AuditLogRequest();
                auditRequest.setAction("DESIGNATION_CHANGED");
                auditRequest.setServiceName("Employee Service");
                auditRequest.setPerformedBy(updatedByUserId);
                auditRequest.setTargetId(id);
                auditRequest.setDescription("Employee designation changed");
                auditRequest.setOldValues(oldValues);
                auditRequest.setNewValues(newValues);
                auditLogClient.createAuditLog(auditRequest);
            } catch (Exception e) {
                // Non-blocking audit
            }
        }

        return savedEmployee;
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Employee getEmployeeByUserId(Long userId) {
        return employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Long getEmployeeIdByUserId(Long userId) {
        return employeeRepository.findByUserId(userId)
                .map(Employee::getId)
                .orElse(null);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getEmployeesByStatus(EmployeeStatus status) {
        return employeeRepository.findByStatus(status);
    }

    /**
     * Soft delete employee (deactivation)
     * Sets status to RESIGNED or TERMINATED
     * Deactivates auth account
     * Preserves payroll & audit history
     */
    public void deactivateEmployee(Long id, EmployeeStatus status, Long deactivatedByUserId) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setStatus(status);
        employee.setExitDate(LocalDate.now());
        employeeRepository.save(employee);

        // Deactivate auth account
        try {
            authenticationClient.deactivateUser(employee.getUserId());
        } catch (Exception e) {
            // Log error but don't fail
        }

        // Audit deactivation
        try {
            Map<String, Object> oldValues = new HashMap<>();
            oldValues.put("status", "ACTIVE");

            Map<String, Object> newValues = new HashMap<>();
            newValues.put("status", status.name());
            newValues.put("exitDate", LocalDate.now().toString());

            AuditLogRequest auditRequest = new AuditLogRequest();
            auditRequest.setAction("EMPLOYEE_DEACTIVATED");
            auditRequest.setServiceName("Employee Service");
            auditRequest.setPerformedBy(deactivatedByUserId);
            auditRequest.setTargetId(id);
            auditRequest.setDescription("Employee deactivated with status: " + status);
            auditRequest.setOldValues(oldValues);
            auditRequest.setNewValues(newValues);
            auditLogClient.createAuditLog(auditRequest);
        } catch (Exception e) {
            // Non-blocking audit
        }
    }

    public Designation getDesignationByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return employee.getDesignation();
    }
}
