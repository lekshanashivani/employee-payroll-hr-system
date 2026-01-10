import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EmployeeService } from '../../../core/services/employee.service';
import { Designation, Employee } from '../../../core/models/employee.model';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-employee-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="page-header">
      <div class="header-left">
        <h2>Employee Details</h2>
        <span class="id-tag">ID: {{ employee?.id }}</span>
      </div>
      <div>
        <button type="button" (click)="toggleEdit()" class="btn-secondary" *ngIf="!isEditing">Edit Profile</button>
        <button type="button" (click)="cancelEdit()" class="btn-secondary" *ngIf="isEditing">Cancel</button>
      </div>
    </div>

    <div *ngIf="!employee" class="loading-state">
      Loading employee details...
    </div>

    <div class="form-container" *ngIf="employee">
      <form [formGroup]="editForm" (ngSubmit)="onSubmit()">
        
        <div class="form-section">
          <div class="section-header">
             <h3>Personal Information</h3>
             <span *ngIf="!isEditing" class="status-badge" [class.active]="employee?.status === 'ACTIVE'">
               {{ employee?.status }}
             </span>
          </div>

          <div class="grid-row">
            <div class="form-group">
              <label>Full Name</label>
              <input type="text" formControlName="name" class="form-control" [readonly]="!isEditing">
            </div>
            <div class="form-group">
              <label>Date of Birth</label>
              <input type="date" formControlName="dateOfBirth" class="form-control" [readonly]="!isEditing">
            </div>
          </div>
          
          <div class="grid-row">
            <div class="form-group">
              <label>Phone Number</label>
              <input type="tel" formControlName="phoneNumber" class="form-control" [readonly]="!isEditing">
            </div>
            <div class="form-group">
              <label>Address</label>
              <input type="text" formControlName="address" class="form-control" [readonly]="!isEditing">
            </div>
          </div>
        </div>

        <div class="form-section">
          <h3>Job Details</h3>
          <div class="grid-row">
            <div class="form-group">
              <label>Department</label>
              <input type="text" formControlName="department" class="form-control" [readonly]="!isEditing">
            </div>
            <div class="form-group">
              <label>Designation</label>
              <select formControlName="designationId" class="form-control" *ngIf="isEditing">
                <option *ngFor="let d of designations" [value]="d.id">
                  {{ d.name }}
                </option>
              </select>
              <input type="text" class="form-control" [value]="employee?.designationName" readonly *ngIf="!isEditing">
            </div>
          </div>
        </div>

        <div class="form-actions" *ngIf="isEditing">
          <button type="submit" [disabled]="loading || editForm.invalid" class="btn-primary">
            {{ loading ? 'Saving...' : 'Save Changes' }}
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
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    .header-left {
      display: flex;
      align-items: baseline;
      gap: 1rem;
    }

    .header-left h2 {
      margin: 0;
      color: var(--text-primary);
    }

    .id-tag {
      color: var(--text-secondary);
      font-family: monospace;
      font-size: 0.875rem;
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

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
    }

    .form-section h3 {
      font-size: 1.1rem;
      color: var(--text-primary);
      margin: 0;
    }

    .status-badge {
      padding: 0.25rem 0.75rem;
      border-radius: 20px;
      font-size: 0.75rem;
      font-weight: 600;
      background: var(--error-bg);
      color: var(--error-color);
    }
    
    .status-badge.active {
      background: var(--success-bg);
      color: var(--success-color);
    }

    .grid-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1.5rem;
      margin-bottom: 1rem;
    }

    .form-group {
      margin-bottom: 0.5rem;
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
    }

    .form-control[readonly] {
      background: var(--bg-secondary);
      opacity: 0.7;
      cursor: default;
    }

    .form-actions {
      display: flex;
      justify-content: flex-end;
      margin-top: 2rem;
    }

    .btn-primary, .btn-secondary {
      padding: 0.5rem 1rem;
      border-radius: 6px;
      font-weight: 500;
      cursor: pointer;
      border: 1px solid transparent;
    }

    .btn-primary {
      background: var(--primary-color);
      color: white;
    }

    .btn-secondary {
      background: white;
      border-color: var(--border-color);
      color: var(--text-primary);
    }
  `]
})
export class EmployeeDetailComponent implements OnInit {
  employee: Employee | null = null;
  designations: Designation[] = [];
  editForm: FormGroup;
  isEditing = false;
  loading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private employeeService: EmployeeService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.editForm = this.fb.group({
      name: ['', Validators.required],
      dateOfBirth: ['', Validators.required],
      phoneNumber: ['', Validators.required],
      address: ['', Validators.required],
      department: ['', Validators.required],
      designationId: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.employeeService.getEmployeeById(id).subscribe({
        next: (emp) => {
          this.employee = emp;
          this.patchForm();
          this.cdr.detectChanges();
        }
      });

      this.employeeService.getAllDesignations().subscribe({
        next: (desigs) => this.designations = desigs
      });
    }
  }

  patchForm() {
    if (this.employee) {
      this.editForm.patchValue({
        name: this.employee.name,
        dateOfBirth: this.employee.dateOfBirth,
        phoneNumber: this.employee.phoneNumber,
        address: this.employee.address,
        department: this.employee.department,
        designationId: this.employee.designationId
      });
    }
  }

  toggleEdit() {
    this.isEditing = true;
  }

  cancelEdit() {
    this.isEditing = false;
    this.patchForm(); // Reset changes
  }

  onSubmit() {
    if (this.editForm.invalid || !this.employee) return;

    this.loading = true;
    const updates = { ...this.editForm.value, designationId: Number(this.editForm.value.designationId) };

    this.employeeService.updateEmployee(this.employee.id, updates)
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (updatedEmp) => {
          this.employee = updatedEmp;

          if (this.designations) {
            const d = this.designations.find(d => d.id === updatedEmp.designationId);
            if (d && this.employee) this.employee.designationName = d.name;
          }

          this.isEditing = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.error = 'Failed to update profile. Please try again.';
          console.error('Update failed', err);
          this.cdr.detectChanges();
        }
      });
  }
}
