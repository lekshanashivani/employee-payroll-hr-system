import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { Attendance, CreateLeaveRequest, LeaveRequest, HrMeetingRequest, CreateMeetingRequest } from '../models/attendance.model';

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
        // Default range: Last 90 days? For now let's just use the specific endpoint
        // If the backend requires dates, we should provide them.
        // Actually, the backend endpoint `getAttendanceByEmployeeAndDateRange` DOES require dates.
        // But the previous implementation called `/attendance/employee/${employeeId}` which failed?
        // Wait, looking at the previous file content for AttendanceController...
        // `getAttendanceByEmployeeAndDateRange` takes `@RequestParam startDate` and `endDate`.
        // The previous frontend service code was: `return this.api.get<Attendance[]>(`/attendance/employee/${employeeId}`);`
        // This mismatch would result in 400 Bad Request if params are missing.
        // I will fix this method too while I am here to send a default range.

        const end = new Date();
        const start = new Date();
        start.setDate(start.getDate() - 30); // Last 30 days

        const startStr = start.toISOString().split('T')[0];
        const endStr = end.toISOString().split('T')[0];

        return this.api.get<Attendance[]>(`/attendance/employee/${employeeId}?startDate=${startStr}&endDate=${endStr}`);
    }

    getAllAttendance(startDate: string, endDate: string): Observable<Attendance[]> {
        return this.api.get<Attendance[]>(`/attendance/all?startDate=${startDate}&endDate=${endDate}`);
    }

    // Leave Requests
    createLeaveRequest(request: CreateLeaveRequest): Observable<LeaveRequest> {
        return this.api.post<LeaveRequest>('/attendance/leave-requests', request);
    }

    getMyLeaveRequests(employeeId: number): Observable<LeaveRequest[]> {
        return this.api.get<LeaveRequest[]>(`/attendance/leave-requests/employee/${employeeId}`);
    }

    getAllPendingLeaveRequests(): Observable<LeaveRequest[]> {
        return this.api.get<LeaveRequest[]>('/attendance/leave-requests/pending');
    }

    approveLeaveRequest(requestId: number, approverId: number): Observable<LeaveRequest> {
        // Backend expects NO body, but Headers (handled by interceptor usually, but we need to ensure X-User-Id is set)
        // Wait, the controller expects:
        // @PathVariable Long id, @RequestHeader("X-User-Id") Long approvedByUserId...
        // The API Gateway/Auth Interceptor handles headers.
        // We just need the PUT request.
        return this.api.put<LeaveRequest>(`/attendance/leave-requests/${requestId}/approve`, {});
    }

    rejectLeaveRequest(requestId: number, rejectorId: number, reason: string): Observable<LeaveRequest> {
        // Controller: @RequestParam String rejectionReason
        return this.api.put<LeaveRequest>(`/attendance/leave-requests/${requestId}/reject?rejectionReason=${encodeURIComponent(reason)}`, {});
    }

    // HR Meetings
    createMeetingRequest(request: CreateMeetingRequest): Observable<HrMeetingRequest> {
        return this.api.post<HrMeetingRequest>('/attendance/hr-meetings', request);
    }

    getMyMeetingRequests(employeeId: number): Observable<HrMeetingRequest[]> {
        return this.api.get<HrMeetingRequest[]>(`/attendance/hr-meetings/employee/${employeeId}`);
    }

    getAllPendingMeetingRequests(): Observable<HrMeetingRequest[]> {
        return this.api.get<HrMeetingRequest[]>('/attendance/hr-meetings/pending');
    }

    getAllScheduledMeetingRequests(): Observable<HrMeetingRequest[]> {
        return this.api.get<HrMeetingRequest[]>('/attendance/hr-meetings/scheduled');
    }

    approveMeetingRequest(requestId: number, approverId: number, scheduledDateTime: string): Observable<HrMeetingRequest> {
        return this.api.put<HrMeetingRequest>(`/attendance/hr-meetings/${requestId}/approve?scheduledDateTime=${scheduledDateTime}`, {});
    }

    rejectMeetingRequest(requestId: number, rejectorId: number, reason: string): Observable<HrMeetingRequest> {
        return this.api.put<HrMeetingRequest>(`/attendance/hr-meetings/${requestId}/reject?rejectionReason=${encodeURIComponent(reason)}`, {});
    }

    concludeMeetingRequest(requestId: number): Observable<HrMeetingRequest> {
        return this.api.put<HrMeetingRequest>(`/attendance/hr-meetings/${requestId}/conclude`, {});
    }
}
