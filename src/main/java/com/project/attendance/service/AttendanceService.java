package com.project.attendance.service;

import com.project.attendance.entity.Attendance;
import com.project.attendance.repository.AttendanceRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository repo;

    public AttendanceService(AttendanceRepository repo){
        this.repo = repo;
    }

    public Attendance markAttendance(Attendance a){
        return repo.save(a);
    }

    public List<Attendance> getAll(){
        return repo.findAll();
    }
}
