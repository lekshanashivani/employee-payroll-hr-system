import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { Attendance, CreateLeaveRequest, LeaveRequest } from '../models/attendance.model';

@Injectable({
    providedIn: 'root'
})
export class AttendanceService {

    constructor(private api: ApiService) { }

    // Attendance
    markAttendance(employeeId: number): Observable<Attendance> {
        const today = new Date().toISOString().split('T')[0];
        return this.api.post<Attendance>('/attendance/mark', { employeeId, date: today });
    }

    getAttendanceHistory(employeeId: number): Observable<Attendance[]> {
        return this.api.get<Attendance[]>(`/attendance/employee/${employeeId}`);
    }

    // Leave Requests
    createLeaveRequest(request: CreateLeaveRequest): Observable<LeaveRequest> {
        return this.api.post<LeaveRequest>('/attendance/leave', request);
    }

    getMyLeaveRequests(employeeId: number): Observable<LeaveRequest[]> {
        return this.api.get<LeaveRequest[]>(`/attendance/leave/employee/${employeeId}`);
    }

    getAllPendingLeaveRequests(): Observable<LeaveRequest[]> {
        return this.api.get<LeaveRequest[]>('/attendance/leave/pending');
    }

    approveLeaveRequest(requestId: number, approverId: number): Observable<LeaveRequest> {
        return this.api.put<LeaveRequest>(`/attendance/leave/${requestId}/approve`, { approvedBy: approverId });
    }

    rejectLeaveRequest(requestId: number, rejectorId: number, reason: string): Observable<LeaveRequest> {
        return this.api.put<LeaveRequest>(`/attendance/leave/${requestId}/reject`, { rejectedBy: rejectorId, reason });
    }
}
