package com.hrpayroll.employee.controller;

import com.hrpayroll.employee.dto.DesignationDTO;
import com.hrpayroll.employee.dto.EmployeeDTO;
import com.hrpayroll.employee.entity.Designation;
import com.hrpayroll.employee.entity.Employee;
import com.hrpayroll.employee.entity.EmployeeStatus;
import com.hrpayroll.employee.service.DesignationService;
import com.hrpayroll.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Employee Controller
 * 
 * Exposes REST endpoints for employee and designation management.
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DesignationService designationService;

    // Employee endpoints
    /**
     * Create employee with user account (combined operation)
     * Creates both user account and employee profile in one call
     * 
     * Authorization:
     * - HR and ADMIN can create employees (role = EMPLOYEE)
     * - Only ADMIN can create HR accounts (role = HR)
     */
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(
            @RequestBody com.hrpayroll.employee.dto.CreateEmployeeWithUserRequest request,
            @RequestHeader("X-User-Id") Long createdByUserId,
            @RequestHeader("X-User-Role") String createdByRole) {
        try {
            Employee created = employeeService.createEmployee(request, createdByUserId, createdByRole);
            EmployeeDTO dto = mapToDTO(created);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (RuntimeException e) {
            // Let framework handle the error details
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable("id") Long id) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            EmployeeDTO dto = mapToDTO(employee);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/employee-id")
    public ResponseEntity<Long> getEmployeeIdByUserId(@RequestParam("userId") Long userId) {
        try {
            Long employeeId = employeeService.getEmployeeIdByUserId(userId);
            return ResponseEntity.ok(employeeId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<EmployeeDTO> getEmployeeByUserId(@PathVariable("userId") Long userId) {
        try {
            Employee employee = employeeService.getEmployeeByUserId(userId);
            EmployeeDTO dto = mapToDTO(employee);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        List<EmployeeDTO> dtos = employees.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable("id") Long id,
            @RequestBody EmployeeDTO employeeDTO,
            @RequestHeader("X-User-Id") Long updatedByUserId,
            @RequestHeader("X-User-Role") String updatedByRole) {
        try {
            Employee updated = employeeService.updateEmployee(id, employeeDTO, updatedByUserId, updatedByRole);
            EmployeeDTO dto = mapToDTO(updated);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateEmployee(
            @PathVariable("id") Long id,
            @RequestParam EmployeeStatus status,
            @RequestHeader("X-User-Id") Long deactivatedByUserId) {
        try {
            employeeService.deactivateEmployee(id, status, deactivatedByUserId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/designation")
    public ResponseEntity<DesignationDTO> getDesignationByEmployeeId(@PathVariable("id") Long id) {
        try {
            Designation designation = employeeService.getDesignationByEmployeeId(id);
            DesignationDTO dto = mapToDTO(designation);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/role")
    public ResponseEntity<String> getEmployeeRole(@PathVariable("id") Long id) {
        try {
            // This endpoint is used by Attendance Service to determine who can approve
            // leaves
            // In production, you'd call Authentication Service to get the role
            // For now, returning a default - this should be enhanced to call Auth Service
            employeeService.getEmployeeById(id); // Verify employee exists
            // Note: This is a simplified implementation
            // In production, you'd need to call Authentication Service to get the actual
            // role
            return ResponseEntity.ok("EMPLOYEE"); // Default, should be enhanced
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<String> getEmployeeEmail(@PathVariable("id") Long id) {
        try {
            // This endpoint is used by Notification Service
            // In production, you'd get email from User in Authentication Service
            // For now, returning a placeholder
            employeeService.getEmployeeById(id); // Verify employee exists
            // Note: Email should come from Authentication Service
            return ResponseEntity.ok("employee" + id + "@company.com"); // Placeholder
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/audience/{audience}")
    public ResponseEntity<List<Long>> getEmployeeIdsByAudience(@PathVariable("audience") String audience) {
        try {
            // This endpoint is used by Notification Service for announcements
            // Returns employee IDs based on target audience (ALL, HR, EMPLOYEE)
            List<Employee> employees;
            if ("ALL".equals(audience)) {
                employees = employeeService.getAllEmployees();
            } else {
                // Filter by status or other criteria
                // For now, returning all active employees
                employees = employeeService.getEmployeesByStatus(EmployeeStatus.ACTIVE);
            }
            List<Long> employeeIds = employees.stream()
                    .map(Employee::getId)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(employeeIds);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Designation endpoints
    @PostMapping("/designations")
    public ResponseEntity<DesignationDTO> createDesignation(@RequestBody Designation designation) {
        try {
            Designation created = designationService.createDesignation(designation);
            DesignationDTO dto = mapToDTO(created);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/designations")
    public ResponseEntity<List<DesignationDTO>> getAllActiveDesignations() {
        List<Designation> designations = designationService.getAllActiveDesignations();
        List<DesignationDTO> dtos = designations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/designations/{id}")
    public ResponseEntity<DesignationDTO> getDesignationById(@PathVariable("id") Long id) {
        try {
            Designation designation = designationService.getDesignationById(id);
            DesignationDTO dto = mapToDTO(designation);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/designations/{id}")
    public ResponseEntity<DesignationDTO> updateDesignation(
            @PathVariable("id") Long id,
            @RequestBody Designation designation) {
        try {
            Designation updated = designationService.updateDesignation(id, designation);
            DesignationDTO dto = mapToDTO(updated);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/designations/{id}/deactivate")
    public ResponseEntity<Void> deactivateDesignation(@PathVariable("id") Long id) {
        try {
            designationService.deactivateDesignation(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Helper methods
    private EmployeeDTO mapToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setUserId(employee.getUserId());
        dto.setName(employee.getName());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setDateOfBirth(employee.getDateOfBirth());
        dto.setAddress(employee.getAddress());
        dto.setDepartment(employee.getDepartment());
        dto.setDesignationId(employee.getDesignation().getId());
        dto.setDesignationName(employee.getDesignation().getName());
        dto.setStatus(employee.getStatus());
        dto.setExitDate(employee.getExitDate());
        return dto;
    }

    private DesignationDTO mapToDTO(Designation designation) {
        DesignationDTO dto = new DesignationDTO();
        dto.setId(designation.getId());
        dto.setName(designation.getName());
        dto.setBaseSalary(designation.getBaseSalary());
        dto.setTaxPercentage(designation.getTaxPercentage());
        dto.setBonusPercentage(designation.getBonusPercentage());
        dto.setActive(designation.getActive());
        return dto;
    }
}
