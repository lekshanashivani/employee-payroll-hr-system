package com.project.attendance.service;

import com.project.attendance.entity.LeaveRequest;
import com.project.attendance.repository.LeaveRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LeaveService {

    private final LeaveRepository repo;

    public LeaveService(LeaveRepository repo){
        this.repo = repo;
    }

    public LeaveRequest applyLeave(LeaveRequest leave){
        leave.setStatus("PENDING");
        return repo.save(leave);
    }

    public List<LeaveRequest> getAll(){
        return repo.findAll();
    }

    public LeaveRequest approve(Long id){
        LeaveRequest l = repo.findById(id).orElse(null);
        if(l != null){
            l.setStatus("APPROVED");
            repo.save(l);
        }
        return l;
    }

    public LeaveRequest reject(Long id){
        LeaveRequest l = repo.findById(id).orElse(null);
        if(l != null){
            l.setStatus("REJECTED");
            repo.save(l);
        }
        return l;
    }
}
