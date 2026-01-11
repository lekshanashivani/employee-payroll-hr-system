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
      <button (click)="downloadCsv()" class="btn-primary" [disabled]="logs.length === 0">Export CSV</button>
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
          <tr *ngFor="let log of displayedLogs">
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
    .page-header { margin-bottom: 2rem; display: flex; justify-content: space-between; align-items: center; }
    .page-header h2 { margin: 0; color: var(--text-primary); }
    .btn-primary { padding: 0.5rem 1rem; background: var(--primary-color); color: white; border: none; border-radius: 4px; cursor: pointer; }
    .btn-primary:disabled { background: var(--text-secondary); cursor: not-allowed; opacity: 0.7; }
    
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
  displayedLogs: AuditLog[] = []; // Subset for UI
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
    this.auditLogService.getAllAuditLogs().subscribe({
      next: (data) => {
        // Sort by Latest First
        data.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
        this.logs = data;

        // Show only last 10 records on UI
        this.displayedLogs = this.logs.slice(0, 10);

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

  downloadCsv() {
    if (this.logs.length === 0) return;

    const headers = ['Timestamp', 'Service', 'Action', 'Description', 'Performed By'];
    const rows = this.logs.map(log => [
      `"${new Date(log.createdAt).toLocaleString()}"`,
      `"${log.serviceName}"`,
      `"${this.toTitleCase(log.action)}"`,
      `"${log.description.replace(/"/g, '""')}"`, // Escape quotes
      `"${this.resolveUserName(log.performedBy)}"`
    ]);

    const csvContent = [
      headers.join(','),
      ...rows.map(row => row.join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    if (link.download !== undefined) {
      const url = URL.createObjectURL(blob);
      link.setAttribute('href', url);
      link.setAttribute('download', 'audit_logs.csv');
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }
}
