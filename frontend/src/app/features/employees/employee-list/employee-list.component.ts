import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EmployeeService } from '../../../core/services/employee.service';
import { Employee } from '../../../core/models/employee.model';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="page-header">
      <h2>Employees</h2>
      <a routerLink="/employees/create" class="btn-primary">Add Employee</a>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Department</th>
            <th>Designation</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let emp of employees">
            <td>
              <div class="emp-name">{{ emp.name }}</div>
              <div class="emp-email">ID: {{ emp.id }}</div>
            </td>
            <td>{{ emp.department }}</td>
            <td>{{ emp.designationName }}</td>
            <td>
              <span class="badge" [class.active]="emp.status === 'ACTIVE'" [class.terminated]="emp.status !== 'ACTIVE'">
                {{ emp.status }}
              </span>
            </td>
            <td>
              <a [routerLink]="['/employees', emp.id]" class="action-link">View</a>
            </td>
          </tr>
          <tr *ngIf="employees.length === 0 && !loading">
            <td colspan="5" class="empty-state">No employees found.</td>
          </tr>
        </tbody>
      </table>
      
      <div *ngIf="loading" class="loading-state">Loading employees...</div>
    </div>
  `,
  styles: [`
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    .page-header h2 {
      margin: 0;
      color: var(--text-primary);
    }

    .btn-primary {
      background: var(--primary-color);
      color: white;
      padding: 0.75rem 1.5rem;
      border-radius: 6px;
      text-decoration: none;
      font-weight: 500;
      transition: background 0.2s;
    }

    .btn-primary:hover {
      background: var(--primary-hover);
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

    .data-table th {
      background: var(--bg-secondary);
      font-weight: 600;
      color: var(--text-secondary);
      font-size: 0.875rem;
    }

    .emp-name {
      font-weight: 500;
      color: var(--text-primary);
    }

    .emp-email {
      font-size: 0.75rem;
      color: var(--text-secondary);
    }

    .badge {
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      font-size: 0.75rem;
      font-weight: 600;
      background: var(--bg-secondary);
      color: var(--text-secondary);
    }

    .badge.active {
      background: var(--success-bg);
      color: var(--success-color);
    }

    .badge.terminated {
      background: var(--error-bg);
      color: var(--error-color);
    }

    .action-link {
      color: var(--primary-color);
      text-decoration: none;
      font-weight: 500;
    }

    .action-link:hover {
      text-decoration: underline;
    }

    .empty-state, .loading-state {
      padding: 2rem;
      text-align: center;
      color: var(--text-secondary);
    }
  `]
})
export class EmployeeListComponent implements OnInit {
  employees: Employee[] = [];
  loading = true;

  constructor(
    private employeeService: EmployeeService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadEmployees();
  }

  loadEmployees() {
    this.employeeService.getAllEmployees().subscribe({
      next: (data) => {
        this.employees = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load employees', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
