import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { AttendanceService } from '../../../core/services/attendance.service';
import { LeaveRequest } from '../../../core/models/attendance.model';

@Component({
    selector: 'app-leave-request',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    template: `
    <div class="split-layout">
      <!-- Left: Request Form -->
      <div class="card">
        <h3>Request Leave</h3>
        <form [formGroup]="leaveForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label>Leave Type</label>
            <select formControlName="type" class="form-control">
              <option value="SICK">Sick Leave</option>
              <option value="CASUAL">Casual Leave</option>
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
              <span class="type">{{ req.type }}</span>
              <p class="reason">{{ req.reason }}</p>
            </div>
          </div>
          <div *ngIf="myRequests.length === 0" class="empty-state">No leave requests found.</div>
        </div>
      </div>
    </div>
  `,
    styles: [`
    .split-layout {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 2rem;
    }

    .card {
      background: var(--surface-color);
      padding: 2rem;
      border-radius: 8px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    }

    h3 { margin-top: 0; color: var(--text-primary); }

    .form-group { margin-bottom: 1rem; }
    
    .grid-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }

    label { display: block; margin-bottom: 0.5rem; font-size: 0.875rem; color: var(--text-secondary); }

    .form-control {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid var(--border-color);
      border-radius: 6px;
    }

    .btn-primary {
      width: 100%;
      padding: 0.75rem;
      background: var(--primary-color);
      color: white;
      border: none;
      border-radius: 6px;
      cursor: pointer;
    }
    
    .btn-primary:disabled { opacity: 0.7; }

    .error-msg { color: var(--error-color); margin-top: 0.5rem; font-size: 0.875rem; }

    .list-container { max-height: 400px; overflow-y: auto; }

    .leave-item {
      border: 1px solid var(--border-color);
      border-radius: 6px;
      padding: 1rem;
      margin-bottom: 1rem;
    }

    .leave-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 0.5rem;
    }

    .date-range { font-weight: 600; color: var(--text-primary); }

    .badge {
      font-size: 0.75rem;
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      font-weight: 600;
    }
    .badge.approved { background: var(--success-bg); color: var(--success-color); }
    .badge.pending { background: #fff7ed; color: #c2410c; }
    .badge.rejected { background: var(--error-bg); color: var(--error-color); }

    .type { font-size: 0.75rem; color: var(--text-secondary); text-transform: uppercase; font-weight: bold; }
    .reason { margin: 0.25rem 0 0; font-size: 0.875rem; color: var(--text-secondary); }
    
    .empty-state { text-align: center; color: var(--text-secondary); padding: 1rem; }
  `]
})
export class LeaveRequestComponent implements OnInit {
    leaveForm: FormGroup;
    myRequests: LeaveRequest[] = [];
    loading = false;
    error = '';
    currentUserId: number = 0;

    constructor(
        private fb: FormBuilder,
        private attendanceService: AttendanceService,
        private authService: AuthService
    ) {
        this.leaveForm = this.fb.group({
            type: ['SICK', Validators.required],
            startDate: ['', Validators.required],
            endDate: ['', Validators.required],
            reason: ['', Validators.required]
        });
    }

    ngOnInit() {
        const user = this.authService.currentUser();
        if (user?.userId) {
            this.currentUserId = user.userId;
            this.loadRequests();
        }
    }

    loadRequests() {
        this.attendanceService.getMyLeaveRequests(this.currentUserId).subscribe({
            next: (data) => this.myRequests = data
        });
    }

    onSubmit() {
        if (this.leaveForm.invalid) return;

        this.loading = true;
        this.error = '';

        const request = {
            employeeId: this.currentUserId,
            ...this.leaveForm.value
        };

        this.attendanceService.createLeaveRequest(request).subscribe({
            next: (newItem) => {
                this.myRequests.unshift(newItem);
                this.loading = false;
                this.leaveForm.reset({ type: 'SICK' });
            },
            error: (err) => {
                this.loading = false;
                this.error = 'Failed to submit request';
            }
        });
    }
}
