package com.project.attendance.controller;

import com.project.attendance.entity.LeaveRequest;
import com.project.attendance.service.LeaveService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/leave")
public class LeaveController {

    private final LeaveService service;

    public LeaveController(LeaveService service){
        this.service = service;
    }

    @PostMapping("/apply")
    public LeaveRequest apply(@RequestBody LeaveRequest leave){
        return service.applyLeave(leave);
    }

    @GetMapping("/all")
    public List<LeaveRequest> getAll(){
        return service.getAll();
    }

    @PutMapping("/approve/{id}")
    public LeaveRequest approve(@PathVariable Long id){
        return service.approve(id);
    }

    @PutMapping("/reject/{id}")
    public LeaveRequest reject(@PathVariable Long id){
        return service.reject(id);
    }
}

