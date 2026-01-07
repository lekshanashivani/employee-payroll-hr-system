package com.hrpayroll.payroll.repository;

import com.hrpayroll.payroll.entity.Bonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Repository
public interface BonusRepository extends JpaRepository<Bonus, Long> {
    List<Bonus> findByEmployeeId(Long employeeId);
    
    @Query("SELECT b FROM Bonus b WHERE b.employeeId = :employeeId " +
           "AND ((b.startDate <= :endDate AND b.endDate >= :startDate))")
    List<Bonus> findActiveBonusesForPeriod(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

