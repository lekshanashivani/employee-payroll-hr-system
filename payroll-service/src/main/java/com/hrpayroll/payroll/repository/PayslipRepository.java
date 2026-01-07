package com.hrpayroll.payroll.repository;

import com.hrpayroll.payroll.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    Optional<Payslip> findByEmployeeIdAndPayPeriod(Long employeeId, YearMonth payPeriod);
    List<Payslip> findByEmployeeId(Long employeeId);
    List<Payslip> findByPayPeriod(YearMonth payPeriod);
}

