package com.hrpayroll.attendance.repository;

import com.hrpayroll.attendance.entity.LeaveRequest;
import com.hrpayroll.attendance.entity.LeaveStatus;
import com.hrpayroll.attendance.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeId(Long employeeId);
    List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);
    List<LeaveRequest> findByStatus(LeaveStatus status);
    
    @Query("SELECT l FROM LeaveRequest l WHERE l.employeeId = :employeeId " +
           "AND l.leaveType = :leaveType AND l.status = 'APPROVED' " +
           "AND ((l.startDate <= :endDate AND l.endDate >= :startDate))")
    List<LeaveRequest> findApprovedUnpaidLeaves(
            @Param("employeeId") Long employeeId,
            @Param("leaveType") LeaveType leaveType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

