import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { AttendanceService } from '../../../core/services/attendance.service';
import { Attendance } from '../../../core/models/attendance.model';
import { finalize } from 'rxjs/operators';


@Component({
  // ... (omitting lengthy metadata for brevity in replacement chunk)
  // Wait, I need to be careful with REPLACE logic.
  // I will just replace the specific Import block and the Class Body.

  selector: 'app-attendance-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-header">
      <h2>My Attendance</h2>
      <button 
        *ngIf="!todayAttendance && !isAdmin()" 
        (click)="markAttendance()" 
        class="btn-primary" 
        [disabled]="marking">
        {{ marking ? 'Marking...' : 'Mark Present Today' }}
      </button>
      <div *ngIf="todayAttendance" class="status-badge active">
        Marked Present for Today
      </div>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th *ngIf="isAdmin()">Employee</th>
            <th>Date</th>
            <th>Status</th>
            <th>Time</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let record of attendanceHistory">
            <td *ngIf="isAdmin()">ID: {{ record.employeeId }}</td>
            <td>{{ record.date | date:'mediumDate' }}</td>
            <td>
              <span class="badge" [class.present]="record.status === 'PRESENT'">
                {{ record.status }}
              </span>
            </td>
            <td>{{ record.clockInTime || '09:00 AM' }}</td>
          </tr>
          <tr *ngIf="attendanceHistory.length === 0">
            <td colspan="3" class="empty-state">No attendance records found.</td>
          </tr>
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    .btn-primary {
      background: var(--primary-color);
      color: white;
      padding: 0.75rem 1.5rem;
      border: none;
      border-radius: 6px;
      font-weight: 600;
      cursor: pointer;
    }
    
    .btn-primary:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }

    .status-badge {
      display: inline-block;
      padding: 0.75rem 1.5rem;
      border-radius: 6px;
      font-weight: 600;
    }
    .status-badge.active {
      background: var(--success-bg);
      color: var(--success-color);
      border: 1px solid var(--success-color);
    }

    .table-container {
      background: var(--surface-color);
      border-radius: 8px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
      overflow: hidden;
    }

    .data-table {
      width: 100%;
      border-collapse: collapse;
    }

    .data-table th, .data-table td {
      padding: 1rem;
      text-align: left;
      border-bottom: 1px solid var(--border-color);
    }
    
    .badge {
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      font-size: 0.75rem;
      font-weight: 600;
      background: var(--bg-secondary);
    }
    
    .badge.present {
      background: var(--success-bg);
      color: var(--success-color);
    }

    .empty-state {
      text-align: center;
      color: var(--text-secondary);
      padding: 2rem;
    }
  `]
})
export class AttendanceListComponent implements OnInit {
  attendanceHistory: Attendance[] = [];
  todayAttendance: Attendance | null = null;
  marking = false;
  currentEmployeeId: number | null = null;

  constructor(
    private attendanceService: AttendanceService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    const user = this.authService.currentUser();
    if (user?.employeeId) {
      this.currentEmployeeId = user.employeeId;
      this.loadAttendance();
    } else if (this.isAdmin()) {
      this.loadAttendance();
    }
  }

  loadAttendance() {
    const isAdmin = this.authService.currentUser()?.role === 'ADMIN';

    const end = new Date();
    const start = new Date();
    start.setDate(1);

    const startStr = start.toISOString().split('T')[0];
    const endStr = end.toISOString().split('T')[0];

    if (isAdmin) {
      this.attendanceService.getAllAttendance(startStr, endStr).subscribe({
        next: (data) => {
          this.attendanceHistory = data;
          this.cdr.detectChanges();
        }
      });
    } else if (this.currentEmployeeId) {
      this.attendanceService.getAttendanceHistory(this.currentEmployeeId).subscribe({
        next: (data) => {
          this.attendanceHistory = data;
          this.checkToday();
          this.cdr.detectChanges();
        }
      });
    }
  }

  checkToday() {
    const todayStr = new Date().toISOString().split('T')[0];
    this.todayAttendance = this.attendanceHistory.find(a => a.date === todayStr) || null;
  }

  markAttendance() {
    if (!this.currentEmployeeId) return;

    this.marking = true;
    this.attendanceService.markAttendance(this.currentEmployeeId)
      .pipe(finalize(() => {
        this.marking = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (newRecord) => {
          this.attendanceHistory.unshift(newRecord);
          this.todayAttendance = newRecord;
        },
        error: (err) => {
          console.error('Failed to mark attendance', err);
        }
      });
  }

  isAdmin(): boolean {
    return this.authService.currentUser()?.role === 'ADMIN';
  }
}
