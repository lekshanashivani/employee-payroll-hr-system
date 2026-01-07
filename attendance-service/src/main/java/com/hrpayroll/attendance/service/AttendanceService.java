package com.hrpayroll.attendance.service;

import com.hrpayroll.attendance.entity.Attendance;
import com.hrpayroll.attendance.entity.AttendanceStatus;
import com.hrpayroll.attendance.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Attendance Service
 * 
 * Handles daily attendance tracking.
 */
@Service
@Transactional
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public Attendance markAttendance(Long employeeId, LocalDate date) {
        // Check if attendance already marked for this date
        attendanceRepository.findByEmployeeIdAndDate(employeeId, date)
                .ifPresent(a -> {
                    throw new RuntimeException("Attendance already marked for this date");
                });

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setDate(date);
        attendance.setStatus(AttendanceStatus.PRESENT);

        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getAttendanceByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate);
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }
}

