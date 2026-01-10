import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuditLogService } from '../../../core/services/audit-log.service';
import { EmployeeService } from '../../../core/services/employee.service';
import { AuditLog } from '../../../core/models/audit-log.model';

@Component({
  selector: 'app-audit-log-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-header">
      <h2>Audit Logs</h2>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>Timestamp</th>
            <th>Service</th>
            <th>Action</th>
            <th>Description</th>
            <th>Performed By</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let log of logs">
            <td>{{ log.createdAt | date:'medium' }}</td>
            <td>
              <span class="service-tag">{{ log.serviceName }}</span>
            </td>
            <td>
                <span class="action-tag">{{ toTitleCase(log.action) }}</span>
            </td>
            <td>{{ log.description }}</td>
            <td>{{ resolveUserName(log.performedBy) }}</td>
          </tr>
          <tr *ngIf="logs.length === 0 && !loading">
            <td colspan="5" class="empty-state">No audit logs found.</td>
          </tr>
        </tbody>
      </table>
      
      <div *ngIf="loading" class="loading-state">Loading audit logs...</div>
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 2rem; }
    .page-header h2 { margin: 0; color: var(--text-primary); }
    
    .table-container {
      background: var(--surface-color);
      border-radius: 8px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
      overflow: hidden;
    }

    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th, .data-table td { padding: 1rem; text-align: left; border-bottom: 1px solid var(--border-color); }
    .data-table th { background: var(--bg-secondary); font-weight: 600; color: var(--text-secondary); font-size: 0.875rem; }
    
    .service-tag { padding: 0.25rem 0.5rem; background: var(--bg-secondary); border-radius: 4px; font-size: 0.75rem; color: var(--text-primary); }
    .action-tag { font-weight: 500; color: var(--primary-color); }
    .empty-state, .loading-state { padding: 2rem; text-align: center; color: var(--text-secondary); }
  `]
})
export class AuditLogListComponent implements OnInit {
  logs: AuditLog[] = [];
  loading = true;
  userMap = new Map<number, string>();

  constructor(
    private auditLogService: AuditLogService,
    private employeeService: EmployeeService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadEmployees();
    this.loadLogs();
  }

  loadEmployees() {
    this.employeeService.getAllEmployees().subscribe({
      next: (employees) => {
        employees.forEach(emp => {
          if (emp.userId) {
            this.userMap.set(emp.userId, emp.name);
          }
        });
        this.cdr.detectChanges();
      }
    });
  }

  loadLogs() {
    // Fetch logs from 'Employee Service' (covers most HR actions)
    // AND 'Authentication Service' (covers logins) 
    this.auditLogService.getAuditLogsByService('Employee%20Service').subscribe({
      next: (data) => {
        this.logs = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load audit logs', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  resolveUserName(userId: number): string {
    return this.userMap.get(userId) || `User ID: ${userId}`;
  }

  toTitleCase(str: string): string {
    return str.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, s => s.toUpperCase());
  }
}
