import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { AttendanceService } from '../../../core/services/attendance.service';
import { HrMeetingRequest } from '../../../core/models/attendance.model';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-meeting-request',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <!-- Admin View: Pending Meetings -->
    <div *ngIf="isHr()" class="card admin-panel">
      <h3>Pending HR Meeting Requests</h3>
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>Employee ID</th>
              <th>Subject</th>
              <th>Preferred Time</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let req of pendingRequests">
              <td>{{ req.employeeId }}</td>
              <td>{{ req.subject }}<br><small class="text-muted">{{ req.description }}</small></td>
              <td>{{ req.preferredDateTime | date:'short' }}</td>
              <td class="actions">
                <button (click)="approve(req)" class="btn-icon approve" title="Approve & Schedule">✓</button>
                <button (click)="reject(req)" class="btn-icon reject" title="Reject">✕</button>
              </td>
            </tr>
            <tr *ngIf="pendingRequests.length === 0">
              <td colspan="4" class="empty-state">No pending meeting requests.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div *ngIf="!isHr()" class="split-layout" style="margin-top: 2rem;">
      <!-- Left: Request Form -->
      <div class="card">
        <h3>Request 1:1 with HR</h3>
        <form [formGroup]="meetingForm" (ngSubmit)="onSubmit()">
          
          <div class="form-group">
            <label>Subject</label>
            <input type="text" formControlName="subject" class="form-control" placeholder="e.g. Salary Discussion">
          </div>

          <div class="form-group">
            <label>Preferred Date & Time</label>
            <input type="datetime-local" formControlName="preferredDateTime" class="form-control">
          </div>

          <div class="form-group">
            <label>Description (Optional)</label>
            <textarea formControlName="description" rows="3" class="form-control" placeholder="Brief details..."></textarea>
          </div>

          <button type="submit" [disabled]="loading || meetingForm.invalid" class="btn-primary">
            {{ loading ? 'Submitting...' : 'Request Meeting' }}
          </button>
          
          <div *ngIf="error" class="error-msg">{{ error }}</div>
        </form>
      </div>

      <!-- Right: My Requests History -->
      <div class="card">
        <h3>My Meeting History</h3>
        <div class="list-container">
          <div *ngFor="let req of myRequests" class="leave-item">
            <div class="leave-header">
              <span class="date-range">{{ req.preferredDateTime | date:'mediumDate' }}</span>
              <span class="badge" [ngClass]="req.status.toLowerCase()">{{ req.status }}</span>
            </div>
            <div class="leave-details">
              <span class="type">{{ req.subject }}</span>
              
              <div *ngIf="req.status === 'APPROVED'" class="approved-info">
                 <strong>Scheduled:</strong> {{ req.scheduledDateTime | date:'medium' }}
              </div>

              <p *ngIf="req.rejectionReason" class="rejection-msg">Reason: {{ req.rejectionReason }}</p>
            </div>
          </div>
          <div *ngIf="myRequests.length === 0" class="empty-state">No meeting requests found.</div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .split-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; }
    .card { background: var(--surface-color); padding: 2rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    h3 { margin-top: 0; color: var(--text-primary); }
    .form-group { margin-bottom: 1rem; }
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
    .type { font-size: 0.875rem; font-weight: bold; color: var(--text-primary); }
    .text-muted { font-size: 0.75rem; color: var(--text-secondary); }
    .approved-info { margin-top: 0.5rem; color: var(--success-color); font-size: 0.9rem; }
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
export class MeetingRequestComponent implements OnInit {
  meetingForm: FormGroup;
  myRequests: HrMeetingRequest[] = [];
  pendingRequests: HrMeetingRequest[] = [];
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
    this.meetingForm = this.fb.group({
      subject: ['', Validators.required],
      description: [''],
      preferredDateTime: ['', Validators.required]
    });
  }

  ngOnInit() {
    const user = this.authService.currentUser();
    if (user) {
      this.currentUserId = user.userId;
      this.currentEmployeeId = user.employeeId || null;

      if (this.currentEmployeeId) {
        this.loadMyRequests();
      }

      if (this.isHr()) {
        this.loadPendingRequests();
      }
    }
  }

  isHr(): boolean {
    return this.authService.currentUser()?.role === 'HR';
  }

  loadMyRequests() {
    if (!this.currentEmployeeId) return;
    this.attendanceService.getMyMeetingRequests(this.currentEmployeeId).subscribe({
      next: (data) => {
        this.myRequests = data;
        this.cdr.detectChanges();
      }
    });
  }

  loadPendingRequests() {
    this.attendanceService.getAllPendingMeetingRequests().subscribe({
      next: (data) => {
        this.pendingRequests = data;
        this.cdr.detectChanges();
      }
    });
  }

  onSubmit() {
    if (this.meetingForm.invalid || !this.currentEmployeeId) return;

    this.loading = true;
    this.error = '';

    const request = {
      employeeId: this.currentEmployeeId,
      ...this.meetingForm.value
    };

    this.attendanceService.createMeetingRequest(request)
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (newItem) => {
          this.myRequests.unshift(newItem);
          this.meetingForm.reset();
        },
        error: (err) => {
          this.error = 'Failed to submit request';
        }
      });
  }

  approve(req: HrMeetingRequest) {
    // For approval, we need to set a scheduled time. Default to preferred time or ask user.
    // Let's ask via a prompt or simple input for now.
    // Since prompt is string, we'll try to use the preferred time as default
    const defaultTime = req.preferredDateTime;
    const scheduledTime = prompt('Confirm Scheduled Date & Time (ISO format YYYY-MM-DDTHH:MM):', defaultTime);

    if (!scheduledTime) return;

    this.attendanceService.approveMeetingRequest(req.id, this.currentUserId, scheduledTime)
      .subscribe({
        next: (updated) => {
          this.pendingRequests = this.pendingRequests.filter(r => r.id !== req.id);
          this.cdr.detectChanges();
          alert(`Meeting Scheduled for ${new Date(scheduledTime).toLocaleString()}`);
        }
      });
  }

  reject(req: HrMeetingRequest) {
    const reason = prompt('Enter rejection reason:');
    if (!reason) return;

    this.attendanceService.rejectMeetingRequest(req.id, this.currentUserId, reason)
      .subscribe({
        next: (updated) => {
          this.pendingRequests = this.pendingRequests.filter(r => r.id !== req.id);
          this.cdr.detectChanges();
        }
      });
  }
}
