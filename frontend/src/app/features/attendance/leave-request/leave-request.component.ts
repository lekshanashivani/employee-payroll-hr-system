import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { AttendanceService } from '../../../core/services/attendance.service';
import { LeaveRequest } from '../../../core/models/attendance.model';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-leave-request',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <!-- Admin View: Pending Requests -->
    <div *ngIf="canApprove" class="card admin-panel">
      <h3>Pending Leave Requests</h3>
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>Employee ID</th>
              <th>Type</th>
              <th>Dates</th>
              <th>Reason</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let req of pendingRequests">
              <td>{{ req.employeeId }}</td>
              <td><span class="badge">{{ req.leaveType }}</span></td>
              <td>{{ req.startDate | date:'shortDate' }} - {{ req.endDate | date:'shortDate' }}</td>
              <td>{{ req.reason }}</td>
              <td class="actions">
                <button (click)="approve(req)" class="btn-icon approve" title="Approve">✓</button>
                <button (click)="reject(req)" class="btn-icon reject" title="Reject">✕</button>
              </td>
            </tr>
            <tr *ngIf="pendingRequests.length === 0">
              <td colspan="5" class="empty-state">No pending requests.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Employee View: Request Form (Only if not Admin or if Admin wants to request for themselves?) 
         For simplicity, let's keep the User layout for everyone, but Admins see the panel above too. -->
    
    <div *ngIf="canRequest" class="split-layout" style="margin-top: 2rem;">
      <!-- Left: Request Form -->
      <div class="card">
        <h3>Request Leave</h3>
        <form [formGroup]="leaveForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label>Leave Type</label>
            <select formControlName="leaveType" class="form-control">
              <option value="PAID">Paid Leave</option>
              <option value="UNPAID">Unpaid Leave</option>
            </select>
          </div>

          <div class="grid-row">
            <div class="form-group">
              <label>Start Date</label>
              <input type="date" formControlName="startDate" class="form-control">
            </div>
            <div class="form-group">
              <label>End Date</label>
              <input type="date" formControlName="endDate" class="form-control">
            </div>
          </div>

          <div class="form-group">
            <label>Reason</label>
            <textarea formControlName="reason" rows="3" class="form-control" placeholder="Brief reason..."></textarea>
          </div>

          <button type="submit" [disabled]="loading || leaveForm.invalid" class="btn-primary">
            {{ loading ? 'Submitting...' : 'Submit Request' }}
          </button>
          
          <div *ngIf="error" class="error-msg">{{ error }}</div>
        </form>
      </div>

      <!-- Right: My Requests History -->
      <div class="card">
        <h3>My Leave History</h3>
        <div class="list-container">
          <div *ngFor="let req of myRequests" class="leave-item">
            <div class="leave-header">
              <span class="date-range">{{ req.startDate | date:'shortDate' }} - {{ req.endDate | date:'shortDate' }}</span>
              <span class="badge" [ngClass]="req.status.toLowerCase()">{{ req.status }}</span>
            </div>
            <div class="leave-details">
              <span class="type">{{ req.leaveType }}</span>
              <p class="reason">{{ req.reason }}</p>
              <p *ngIf="req.rejectionReason" class="rejection-msg">Reason: {{ req.rejectionReason }}</p>
            </div>
          </div>
          <div *ngIf="myRequests.length === 0" class="empty-state">No leave requests found.</div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .split-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; }
    .card { background: var(--surface-color); padding: 2rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    h3 { margin-top: 0; color: var(--text-primary); }
    .form-group { margin-bottom: 1rem; }
    .grid-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    label { display: block; margin-bottom: 0.5rem; font-size: 0.875rem; color: var(--text-secondary); }
    .form-control { width: 100%; padding: 0.75rem; border: 1px solid var(--border-color); border-radius: 6px; }
    .btn-primary { width: 100%; padding: 0.75rem; background: var(--primary-color); color: white; border: none; border-radius: 6px; cursor: pointer; }
    .btn-primary:disabled { opacity: 0.7; }
    .error-msg { color: var(--error-color); margin-top: 0.5rem; font-size: 0.875rem; }
    .list-container { max-height: 400px; overflow-y: auto; }
    .leave-item { border: 1px solid var(--border-color); border-radius: 6px; padding: 1rem; margin-bottom: 1rem; }
    .leave-header { display: flex; justify-content: space-between; margin-bottom: 0.5rem; }
    .date-range { font-weight: 600; color: var(--text-primary); }
    .badge { font-size: 0.75rem; padding: 0.25rem 0.5rem; border-radius: 4px; font-weight: 600; background: var(--bg-secondary); }
    .badge.approved { background: var(--success-bg); color: var(--success-color); }
    .badge.pending { background: #fff7ed; color: #c2410c; }
    .badge.rejected { background: var(--error-bg); color: var(--error-color); }
    .type { font-size: 0.75rem; color: var(--text-secondary); text-transform: uppercase; font-weight: bold; }
    .reason { margin: 0.25rem 0 0; font-size: 0.875rem; color: var(--text-secondary); }
    .rejection-msg { color: var(--error-color); font-size: 0.8rem; margin-top: 0.25rem; font-style: italic; }
    .empty-state { text-align: center; color: var(--text-secondary); padding: 1rem; }
    
    /* Admin Table Styles */
    .admin-panel { border-left: 4px solid var(--primary-color); }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th, .data-table td { padding: 0.75rem; text-align: left; border-bottom: 1px solid var(--border-color); }
    .actions { display: flex; gap: 0.5rem; }
    .btn-icon { width: 32px; height: 32px; border-radius: 50%; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; font-weight: bold; }
    .btn-icon.approve { background: var(--success-bg); color: var(--success-color); }
    .btn-icon.reject { background: var(--error-bg); color: var(--error-color); }
    .btn-icon:hover { opacity: 0.8; }
  `]
})
export class LeaveRequestComponent implements OnInit {
  leaveForm: FormGroup;
  myRequests: LeaveRequest[] = [];
  pendingRequests: LeaveRequest[] = [];
  loading = false;
  error = '';
  currentEmployeeId: number | null = null;
  currentUserId: number = 0;

  constructor(
    private fb: FormBuilder,
    private attendanceService: AttendanceService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {
    this.leaveForm = this.fb.group({
      leaveType: ['PAID', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      reason: ['', Validators.required]
    });
  }

  ngOnInit() {
    const user = this.authService.currentUser();
    if (user) {
      this.currentUserId = user.userId;
      this.currentEmployeeId = user.employeeId || null; // Fix: Use employeeId

      if (this.currentEmployeeId) {
        this.loadMyRequests();
      }

      if (this.canApprove) {
        this.loadPendingRequests();
      }
    }
  }

  // Check if user has permission to approve/reject leaves (Admin or HR)
  get canApprove(): boolean {
    const role = this.authService.currentUser()?.role;
    return role === 'ADMIN' || role === 'HR';
  }

  // Check if user can request leave (Everyone except purely admin users if we wanted, but let's allow everyone)
  // Actually, usually Admin accounts are system accounts. But let's allow all authentic employees.
  get canRequest(): boolean {
    return !!this.currentEmployeeId;
  }

  loadMyRequests() {
    if (!this.currentEmployeeId) return;
    this.attendanceService.getMyLeaveRequests(this.currentEmployeeId).subscribe({
      next: (data) => {
        this.myRequests = data;
        this.cdr.detectChanges();
      }
    });
  }

  loadPendingRequests() {
    this.attendanceService.getAllPendingLeaveRequests().subscribe({
      next: (data) => {
        this.pendingRequests = data;
        this.cdr.detectChanges();
      }
    });
  }

  onSubmit() {
    if (this.leaveForm.invalid || !this.currentEmployeeId) return;

    this.loading = true;
    this.error = '';

    const request: any = {
      employeeId: this.currentEmployeeId, // Fix: Use correct ID
      ...this.leaveForm.value
    };

    this.attendanceService.createLeaveRequest(request)
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (newItem) => {
          this.myRequests.unshift(newItem);
          this.leaveForm.reset({ leaveType: 'PAID' });
        },
        error: (err) => {
          this.loading = false;
          // Try to extract error message from backend
          const errorMsg = typeof err.error === 'string' ? err.error : 'Failed to submit request';
          this.error = errorMsg;
          console.error('Leave Request Error:', err);
        }
      });
  }

  approve(req: LeaveRequest) {
    if (!confirm('Approve this leave request?')) return;

    this.attendanceService.approveLeaveRequest(req.id, this.currentUserId) // Admin acts as User here
      .subscribe({
        next: (updated) => {
          this.pendingRequests = this.pendingRequests.filter(r => r.id !== req.id);
          this.cdr.detectChanges();
        }
      });
  }

  reject(req: LeaveRequest) {
    const reason = prompt('Enter rejection reason:');
    if (!reason) return;

    this.attendanceService.rejectLeaveRequest(req.id, this.currentUserId, reason)
      .subscribe({
        next: (updated) => {
          this.pendingRequests = this.pendingRequests.filter(r => r.id !== req.id);
          this.cdr.detectChanges();
        }
      });
  }
}
