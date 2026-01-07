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
import java.util.stream.Collectors;

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

    public Employee createEmployee(Employee employee) {
        if (employeeRepository.existsByUserId(employee.getUserId())) {
            throw new RuntimeException("Employee with this userId already exists");
        }

        Designation designation = designationRepository.findById(employee.getDesignation().getId())
                .orElseThrow(() -> new RuntimeException("Designation not found"));

        employee.setDesignation(designation);
        return employeeRepository.save(employee);
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
        String oldName = employee.getName();
        String oldDepartment = employee.getDepartment();
        EmployeeStatus oldStatus = employee.getStatus();

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

