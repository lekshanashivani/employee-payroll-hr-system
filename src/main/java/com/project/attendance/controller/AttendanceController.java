package com.project.attendance.controller;

import com.project.attendance.entity.Attendance;
import com.project.attendance.service.AttendanceService;
import com.project.attendance.feign.EmployeeClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService service;
    private final EmployeeClient employeeClient;

    public AttendanceController(AttendanceService service, EmployeeClient employeeClient){
        this.service = service;
        this.employeeClient = employeeClient;
    }

    @PostMapping("/mark")
    public Attendance mark(@RequestBody Attendance a){
        return service.markAttendance(a);
    }

    @GetMapping("/all")
    public List<Attendance> all(){
        return service.getAll();
    }

    @GetMapping("/check-employee")
    @CircuitBreaker(name = "employeeServiceBreaker", fallbackMethod = "employeeFallback")
    public String check(){
        return employeeClient.testEmployee();
    }

    public String employeeFallback(Exception e){
        return "Employee Service Down. Try Later.";
    }
}
