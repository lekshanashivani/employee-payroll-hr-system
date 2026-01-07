package com.hrpayroll.payroll.controller;

import com.hrpayroll.payroll.entity.Bonus;
import com.hrpayroll.payroll.entity.Payslip;
import com.hrpayroll.payroll.service.BonusService;
import com.hrpayroll.payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

/**
 * Payroll Controller
 * 
 * Exposes REST endpoints for payroll and bonus management.
 */
@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private BonusService bonusService;

    @PostMapping("/payslips/generate")
    public ResponseEntity<Payslip> generatePayslip(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth payPeriod,
            @RequestHeader("X-User-Id") Long generatedByUserId) {
        try {
            Payslip payslip = payrollService.generatePayslip(employeeId, payPeriod, generatedByUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(payslip);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/payslips/{id}")
    public ResponseEntity<Payslip> getPayslipById(@PathVariable Long id) {
        try {
            Payslip payslip = payrollService.getPayslipById(id);
            return ResponseEntity.ok(payslip);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/payslips/employee/{employeeId}")
    public ResponseEntity<List<Payslip>> getPayslipsByEmployee(@PathVariable Long employeeId) {
        List<Payslip> payslips = payrollService.getPayslipsByEmployee(employeeId);
        return ResponseEntity.ok(payslips);
    }

    @GetMapping("/payslips/period/{payPeriod}")
    public ResponseEntity<List<Payslip>> getPayslipsByPayPeriod(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth payPeriod) {
        List<Payslip> payslips = payrollService.getPayslipsByPayPeriod(payPeriod);
        return ResponseEntity.ok(payslips);
    }

    @PostMapping("/bonuses")
    public ResponseEntity<Bonus> grantBonus(
            @RequestBody Bonus bonus,
            @RequestHeader("X-User-Id") Long grantedByUserId) {
        try {
            Bonus granted = bonusService.grantBonus(bonus, grantedByUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(granted);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/bonuses/employee/{employeeId}")
    public ResponseEntity<List<Bonus>> getBonusesByEmployee(@PathVariable Long employeeId) {
        List<Bonus> bonuses = bonusService.getBonusesByEmployee(employeeId);
        return ResponseEntity.ok(bonuses);
    }

    @GetMapping("/bonuses/{id}")
    public ResponseEntity<Bonus> getBonusById(@PathVariable Long id) {
        try {
            Bonus bonus = bonusService.getBonusById(id);
            return ResponseEntity.ok(bonus);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

