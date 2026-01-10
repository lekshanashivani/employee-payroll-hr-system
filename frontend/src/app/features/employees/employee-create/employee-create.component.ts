import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { EmployeeService } from '../../../core/services/employee.service';
import { Designation } from '../../../core/models/employee.model';

@Component({
  selector: 'app-employee-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="page-header">
      <h2>Add New Employee</h2>
    </div>

    <div class="form-container">
      <form [formGroup]="employeeForm" (ngSubmit)="onSubmit()">
        
        <!-- Section: Account Info -->
        <div class="form-section">
          <h3>Account Information</h3>
          <div class="grid-row">
            <div class="form-group">
              <label>Email</label>
              <input type="email" formControlName="email" class="form-control" placeholder="john@company.com">
            </div>
            <div class="form-group">
              <label>Password</label>
              <input type="password" formControlName="password" class="form-control" placeholder="••••••••">
            </div>
          </div>
          <div class="form-group">
            <label>Role</label>
            <select formControlName="role" class="form-control">
              <option value="EMPLOYEE">Employee</option>
              <option value="HR">HR Manager</option>
              <option value="ADMIN">Administrator</option>
            </select>
          </div>
        </div>

        <!-- Section: Personal Info -->
        <div class="form-section">
          <h3>Personal Information</h3>
          <div class="grid-row">
            <div class="form-group">
              <label>Full Name</label>
              <input type="text" formControlName="name" class="form-control" placeholder="John Doe">
            </div>
            <div class="form-group">
              <label>Date of Birth</label>
              <input type="date" formControlName="dateOfBirth" class="form-control">
            </div>
          </div>
          <div class="grid-row">
            <div class="form-group">
              <label>Phone Number</label>
              <input type="tel" formControlName="phoneNumber" class="form-control" placeholder="+1 234 567 8900">
            </div>
            <div class="form-group">
              <label>Address</label>
              <input type="text" formControlName="address" class="form-control" placeholder="123 Main St">
            </div>
          </div>
        </div>

        <!-- Section: Job Info -->
        <div class="form-section">
          <h3>Job Details</h3>
          <div class="grid-row">
            <div class="form-group">
              <label>Department</label>
              <input type="text" formControlName="department" class="form-control" placeholder="Engineering">
            </div>
            <div class="form-group">
              <label>Designation</label>
              <select formControlName="designationId" class="form-control">
                <option value="">Select Designation</option>
                <option *ngFor="let d of designations" [value]="d.id">
                  {{ d.name }} (Base: {{ d.baseSalary | currency }})
                </option>
              </select>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="form-actions">
           <a routerLink="/employees" class="btn-secondary">Cancel</a>
           <button type="submit" [disabled]="loading || employeeForm.invalid" class="btn-primary">
             {{ loading ? 'Creating...' : 'Create Employee' }}
           </button>
        </div>

        <div *ngIf="error" class="error-message">
          {{ error }}
        </div>
      </form>
    </div>
  `,
  styles: [`
    .page-header {
      margin-bottom: 2rem;
    }

    .form-container {
      background: var(--surface-color);
      padding: 2rem;
      border-radius: 8px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
      max-width: 800px;
    }

    .form-section {
      margin-bottom: 2rem;
      padding-bottom: 1rem;
      border-bottom: 1px solid var(--border-color);
    }

    .form-section h3 {
      font-size: 1.1rem;
      color: var(--text-primary);
      margin-bottom: 1rem;
    }

    .grid-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1.5rem;
    }

    .form-group {
      margin-bottom: 1rem;
    }

    label {
      display: block;
      margin-bottom: 0.5rem;
      color: var(--text-secondary);
      font-size: 0.875rem;
      font-weight: 500;
    }

    .form-control {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid var(--border-color);
      border-radius: 6px;
      font-size: 0.875rem;
      background: var(--bg-secondary);
      color: var(--text-primary);
    }

    .form-control:focus {
      outline: none;
      border-color: var(--primary-color);
      box-shadow: 0 0 0 3px var(--primary-color-dim);
    }

    .form-actions {
      display: flex;
      justify-content: flex-end;
      gap: 1rem;
      margin-top: 2rem;
    }

    .btn-primary, .btn-secondary {
      padding: 0.75rem 1.5rem;
      border-radius: 6px;
      font-weight: 500;
      cursor: pointer;
      text-decoration: none;
      display: inline-block;
      border: none;
    }

    .btn-primary {
      background: var(--primary-color);
      color: white;
    }
    .btn-primary:hover:not(:disabled) {
      background: var(--primary-hover);
    }
    .btn-primary:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }

    .btn-secondary {
      background: transparent;
      border: 1px solid var(--border-color);
      color: var(--text-secondary);
    }
    .btn-secondary:hover {
      background: var(--bg-secondary);
    }

    .error-message {
      margin-top: 1rem;
      padding: 0.75rem;
      background: var(--error-bg);
      color: var(--error-color);
      border-radius: 4px;
      text-align: center;
    }
  `]
})
export class EmployeeCreateComponent implements OnInit {
  employeeForm: FormGroup;
  designations: Designation[] = [];
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    private router: Router
  ) {
    this.employeeForm = this.fb.group({
      // Account
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['EMPLOYEE', Validators.required],

      // Personal
      name: ['', Validators.required],
      dateOfBirth: ['', Validators.required],
      phoneNumber: ['', Validators.required],
      address: ['', Validators.required],

      // Job
      department: ['', Validators.required],
      designationId: ['', Validators.required],
      status: ['ACTIVE']
    });
  }

  ngOnInit() {
    this.loadDesignations();
  }

  loadDesignations() {
    this.employeeService.getAllDesignations().subscribe({
      next: (data) => this.designations = data,
      error: (err) => console.error('Failed to load designations', err)
    });
  }

  onSubmit() {
    if (this.employeeForm.invalid) return;

    this.loading = true;
    this.error = '';

    const formData = this.employeeForm.value;

    // Transform form data to match API expectation
    const request = {
      ...formData,
      designationId: parseInt(formData.designationId)
    };

    this.employeeService.createEmployee(request).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/employees']);
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Failed to create employee. Please try again.';
        console.error(err);
      }
    });
  }
}
