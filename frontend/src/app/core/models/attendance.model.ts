export interface Attendance {
    id: number;
    employeeId: number;
    date: string;
    status: 'PRESENT' | 'ABSENT' | 'LEAVE';
    clockInTime?: string; // Optional if we add time tracking later
    clockOutTime?: string;
}

export interface LeaveRequest {
    id: number;
    employeeId: number;
    employeeName?: string; // For display
    startDate: string;
    endDate: string;
    reason: string;
    type: 'SICK' | 'CASUAL' | 'UNPAID';
    status: 'PENDING' | 'APPROVED' | 'REJECTED';
    approvedBy?: number;
    rejectionReason?: string;
    createdAt: string;
}

export interface CreateLeaveRequest {
    employeeId: number;
    startDate: string;
    endDate: string;
    reason: string;
    type: 'SICK' | 'CASUAL' | 'UNPAID';
}
