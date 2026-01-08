package com.project.attendance.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;
    private String date;
    private String status;

    public Attendance(){}

    public Attendance(Long employeeId, String date, String status){
        this.employeeId = employeeId;
        this.date = date;
        this.status = status;
    }

    public Long getId(){ return id; }
    public Long getEmployeeId(){ return employeeId; }
    public String getDate(){ return date; }
    public String getStatus(){ return status; }

    public void setId(Long id){ this.id = id; }
    public void setEmployeeId(Long employeeId){ this.employeeId = employeeId; }
    public void setDate(String date){ this.date = date; }
    public void setStatus(String status){ this.status = status; }
}
