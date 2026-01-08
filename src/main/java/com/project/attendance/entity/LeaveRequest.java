package com.project.attendance.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_request")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;
    private String fromDate;
    private String toDate;
    private String reason;
    private String status;

    public LeaveRequest(){}

    public LeaveRequest(Long employeeId, String fromDate, String toDate, String reason, String status){
        this.employeeId = employeeId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.status = status;
    }

    public Long getId(){ return id; }
    public Long getEmployeeId(){ return employeeId; }
    public String getFromDate(){ return fromDate; }
    public String getToDate(){ return toDate; }
    public String getReason(){ return reason; }
    public String getStatus(){ return status; }

    public void setId(Long id){ this.id = id; }
    public void setEmployeeId(Long employeeId){ this.employeeId = employeeId; }
    public void setFromDate(String fromDate){ this.fromDate = fromDate; }
    public void setToDate(String toDate){ this.toDate = toDate; }
    public void setReason(String reason){ this.reason = reason; }
    public void setStatus(String status){ this.status = status; }
}
