import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { EmployeeService } from '../../core/services/employee.service';
import { AuditLogService } from '../../core/services/audit-log.service';
import { NotificationService, Announcement } from '../../core/services/notification.service';
import { Employee } from '../../core/models/employee.model';
import { AuditLog } from '../../core/models/audit-log.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard-header">
      <h1>Dashboard</h1>
      <p>Welcome back, {{ getUserName() }}</p>
    </div>

    <!-- Admin View: Stats Grid -->
    <div class="stats-grid" *ngIf="isAdmin()">
      <div class="stat-card">
        <h3>Total Employees</h3>
        <p class="value">{{ stats.total }}</p>
      </div>
      <div class="stat-card highlight-green">
        <h3>Active</h3>
        <p class="value">{{ stats.active }}</p>
      </div>
      <div class="stat-card highlight-red">
        <h3>Inactive</h3>
        <p class="value">{{ stats.inactive }}</p>
      </div>
    </div>

    <!-- Employee View: Personal Stats -->
    <div class="stats-grid" *ngIf="!isAdmin() && myProfile">
       <div class="stat-card">
         <h3>My Department</h3>
         <p class="value text-sm">{{ myProfile.department }}</p>
       </div>
       <div class="stat-card">
         <h3>My Designation</h3>
         <p class="value text-sm">{{ myProfile.designationName }}</p>
       </div>
       <div class="stat-card" [class.highlight-green]="myProfile.status === 'ACTIVE'">
         <h3>My Status</h3>
         <p class="value">{{ myProfile.status }}</p>
       </div>
    </div>

    <!-- Announcements Section -->
    <div class="section-container" style="margin-bottom: 2rem;">
      <div class="section-header">
        <h2>📢 Announcements</h2>
      </div>
      
      <div class="activity-list">
        <div class="activity-item" *ngFor="let ann of announcements">
          <div class="activity-content">
            <p class="activity-title" style="margin-bottom: 0.5rem;">
              <span class="action" style="color: var(--primary-color);">{{ ann.title }}</span>
              <span class="time">{{ ann.createdAt | date:'short' }}</span>
            </p>
            <p class="activity-desc">{{ ann.message }}</p>
          </div>
        </div>
        
        <div *ngIf="announcements.length === 0" class="empty-state">
          No active announcements.
        </div>
      </div>
    </div>

    <!-- Admin View: Recent Activity -->
    <div class="section-container" *ngIf="isAdmin()">
      <div class="section-header">
        <h2>Recent Activity</h2>
      </div>
      
      <div class="activity-list">
        <div class="activity-item" *ngFor="let log of recentActivity">
          <div class="activity-icon">
            <span class="dot"></span>
          </div>
          <div class="activity-content">
            <p class="activity-title">
              <span class="action">{{ formatAction(log.action) }}</span>
              <span class="time">{{ log.createdAt | date:'short' }}</span>
            </p>
            <p class="activity-desc">
              {{ log.description }}
              <span class="performed-by">by {{ resolveUserName(log.performedBy) }}</span>
            </p>
          </div>
        </div>
        
        <div *ngIf="recentActivity.length === 0" class="empty-state">
          No recent activity found.
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-header { margin-bottom: 2rem; }
    .dashboard-header h1 { margin: 0; color: var(--text-primary); }
    .dashboard-header p { color: var(--text-secondary); margin-top: 0.5rem; }

    .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1.5rem;
        margin-bottom: 2.5rem;
    }

    .stat-card {
        background: var(--surface-color);
        padding: 1.5rem;
        border-radius: 8px;
        border: 1px solid var(--border-color);
        box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }
    .stat-card h3 { margin: 0; font-size: 0.875rem; color: var(--text-secondary); font-weight: 500; }
    .stat-card .value { margin: 0.5rem 0 0 0; font-size: 2rem; font-weight: 600; color: var(--text-primary); }
    .stat-card .value.text-sm { font-size: 1.25rem; margin-top: 0.75rem; }
    .highlight-green .value { color: #10B981; }
    .highlight-red .value { color: #EF4444; }

    .section-container {
        background: var(--surface-color);
        border-radius: 8px;
        border: 1px solid var(--border-color);
        padding: 1.5rem;
    }
    .section-header h2 { margin: 0 0 1.5rem 0; font-size: 1.25rem; color: var(--text-primary); }

    .activity-list { display: flex; flex-direction: column; gap: 1rem; }
    .activity-item { display: flex; gap: 1rem; padding-bottom: 1rem; border-bottom: 1px solid var(--border-color); }
    .activity-item:last-child { border-bottom: none; padding-bottom: 0; }
    
    .activity-icon { padding-top: 0.25rem; }
    .dot { display: block; width: 8px; height: 8px; background: var(--primary-color); border-radius: 50%; }
    
    .activity-content { flex: 1; }
    .activity-title { margin: 0 0 0.25rem 0; font-size: 0.875rem; display: flex; justify-content: space-between; }
    .action { font-weight: 600; color: var(--text-primary); }
    .time { color: var(--text-secondary); font-size: 0.75rem; }
    .activity-desc { margin: 0; font-size: 0.875rem; color: var(--text-secondary); }
    .performed-by { font-size: 0.75rem; color: var(--text-secondary); margin-left: 0.5rem; font-style: italic; }
    
    .empty-state { text-align: center; color: var(--text-secondary); padding: 1rem; }
  `]
})
export class DashboardComponent implements OnInit {
  stats = { total: 0, active: 0, inactive: 0 };
  recentActivity: AuditLog[] = [];
  announcements: Announcement[] = [];
  userMap = new Map<number, string>();
  myProfile: Employee | null = null;

  constructor(
    private authService: AuthService,
    private employeeService: EmployeeService,
    private auditLogService: AuditLogService,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadAnnouncements(); // Load for everyone

    if (this.isAdmin()) {
      this.loadStats();
      this.loadActivity();
    } else {
      this.loadMyProfile();
    }
  }

  loadAnnouncements() {
    this.notificationService.getActiveAnnouncements().subscribe({
      next: (data) => {
        this.announcements = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Failed to load announcements', err)
    });
  }

  isAdmin(): boolean {
    return this.authService.currentUser()?.role === 'ADMIN';
  }

  getUserName(): string {
    return this.authService.currentUser()?.email?.split('@')[0] || 'User';
  }

  loadMyProfile() {
    const employeeId = this.authService.currentUser()?.employeeId;
    if (employeeId) {
      this.employeeService.getEmployeeById(employeeId).subscribe({
        next: (emp) => {
          this.myProfile = emp;
          this.cdr.detectChanges();
        }
      });
    }
  }

  loadStats() {
    this.employeeService.getAllEmployees().subscribe({
      next: (employees) => {
        this.stats.total = employees.length;
        this.stats.active = employees.filter(e => e.status === 'ACTIVE').length;
        this.stats.inactive = this.stats.total - this.stats.active;

        // Build user map
        employees.forEach(emp => {
          if (emp.userId) {
            this.userMap.set(emp.userId, emp.name);
          }
        });

        this.cdr.detectChanges();
      },
      error: () => console.error('Failed to load employee stats')
    });
  }

  loadActivity() {
    this.auditLogService.getAuditLogsByService('Employee%20Service').subscribe({
      next: (logs) => {
        this.recentActivity = logs.slice(0, 5);
        this.cdr.detectChanges();
      },
      error: () => console.error('Failed to load activity')
    });
  }

  formatAction(action: string): string {
    return action.replace(/_/g, ' ');
  }

  resolveUserName(userId: number): string {
    return this.userMap.get(userId) || `User ID: ${userId}`;
  }
}
