package com.hrpayroll.employee.repository;

import com.hrpayroll.employee.entity.Employee;
import com.hrpayroll.employee.entity.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUserId(Long userId);
    List<Employee> findByStatus(EmployeeStatus status);
    boolean existsByUserId(Long userId);
}

