import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { EmployeeService } from '../../../core/services/employee.service';
import { Designation } from '../../../core/models/employee.model';

@Component({
  selector: 'app-designations',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="page-header">
      <h2>Designations</h2>
      <button (click)="openCreateModal()" class="btn-primary" *ngIf="!showForm">Add New Designation</button>
    </div>

    <!-- Create/Edit Form (Inline for simplicity) -->
    <div class="form-card" *ngIf="showForm">
      <h3>{{ isEditing ? 'Edit' : 'Create' }} Designation</h3>
      <form [formGroup]="designationForm" (ngSubmit)="onSubmit()">
        <div class="grid-row">
          <div class="form-group">
            <label>Name</label>
            <input type="text" formControlName="name" class="form-control" placeholder="Software Engineer">
          </div>
          <div class="form-group">
            <label>Base Salary</label>
            <input type="number" formControlName="baseSalary" class="form-control">
          </div>
        </div>
        <div class="grid-row">
          <div class="form-group">
            <label>Tax %</label>
            <input type="number" formControlName="taxPercentage" class="form-control" step="0.1">
          </div>
          <div class="form-group">
            <label>Bonus %</label>
            <input type="number" formControlName="bonusPercentage" class="form-control" step="0.1">
          </div>
        </div>
        
        <div class="form-actions">
          <button type="button" (click)="cancelForm()" class="btn-secondary">Cancel</button>
          <button type="submit" [disabled]="loading || designationForm.invalid" class="btn-primary">
            {{ loading ? 'Saving...' : 'Save Designation' }}
          </button>
        </div>
        <div *ngIf="error" class="error-msg">{{ error }}</div>
      </form>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Base Salary</th>
            <th>Tax</th>
            <th>Bonus</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let d of designations">
            <td>{{ d.name }}</td>
            <td>{{ d.baseSalary | currency }}</td>
            <td>{{ d.taxPercentage }}%</td>
            <td>{{ d.bonusPercentage }}%</td>
            <td>
              <span class="badge" [class.active]="d.active">{{ d.active ? 'Active' : 'Inactive' }}</span>
            </td>
            <td>
              <button (click)="editDesignation(d)" class="btn-link">Edit</button>
            </td>
          </tr>
          <tr *ngIf="designations.length === 0">
            <td colspan="6" class="empty-state">No designations found.</td>
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

    .form-card {
      background: var(--surface-color);
      padding: 1.5rem;
      border-radius: 8px;
      margin-bottom: 2rem;
      border: 1px solid var(--border-color);
    }

    .grid-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    .form-group { margin-bottom: 1rem; }
    label { display: block; margin-bottom: 0.5rem; font-size: 0.875rem; color: var(--text-secondary); }
    
    .form-control {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid var(--border-color);
      border-radius: 6px;
    }

    .form-actions { display: flex; justify-content: flex-end; gap: 1rem; margin-top: 1rem; }

    .btn-primary { background: var(--primary-color); color: white; padding: 0.75rem 1.5rem; border-radius: 6px; border: none; cursor: pointer; }
    .btn-secondary { background: white; color: var(--text-primary); border: 1px solid var(--border-color); padding: 0.75rem 1.5rem; border-radius: 6px; cursor: pointer; }

    .table-container { 
      background: var(--surface-color); 
      border-radius: 8px; 
      box-shadow: 0 1px 3px rgba(0,0,0,0.1); 
      overflow: hidden; 
    }
    
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th, .data-table td { padding: 1rem; text-align: left; border-bottom: 1px solid var(--border-color); }

    .btn-link { background: none; border: none; color: var(--primary-color); cursor: pointer; text-decoration: underline; }
    .badge { padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.75rem; background: var(--error-bg); color: var(--error-color); }
    .badge.active { background: var(--success-bg); color: var(--success-color); }
    
    .error-msg { color: var(--error-color); margin-top: 0.5rem; }
  `]
})
export class DesignationListComponent implements OnInit {
  designations: Designation[] = [];
  designationForm: FormGroup;
  showForm = false;
  isEditing = false;
  loading = false;
  error = '';
  editingId: number | null = null;

  constructor(
    private employeeService: EmployeeService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.designationForm = this.fb.group({
      name: ['', Validators.required],
      baseSalary: ['', Validators.required],
      taxPercentage: ['', Validators.required],
      bonusPercentage: ['', Validators.required],
      active: [true]
    });
  }

  ngOnInit() {
    this.loadDesignations();
  }

  loadDesignations() {
    this.employeeService.getAllDesignations().subscribe({
      next: (data) => {
        this.designations = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Failed to load designations', err)
    });
  }

  openCreateModal() {
    this.showForm = true;
    this.isEditing = false;
    this.editingId = null;
    this.designationForm.reset({ active: true });
  }

  cancelForm() {
    this.showForm = false;
    this.error = '';
  }

  editDesignation(d: Designation) {
    this.showForm = true;
    this.isEditing = true;
    this.editingId = d.id;
    this.designationForm.patchValue(d);
  }

  onSubmit() {
    if (this.designationForm.invalid) return;
    this.loading = true;
    this.error = '';

    const payload = this.designationForm.value;

    if (this.isEditing && this.editingId) {
      this.employeeService.updateDesignation(this.editingId, payload).subscribe({
        next: (updated) => {
          const index = this.designations.findIndex(d => d.id === this.editingId);
          this.designations[index] = updated;
          this.reset();
          this.cdr.detectChanges();
        },
        error: () => {
          this.loading = false;
          this.error = 'Failed to update designation';
          this.cdr.detectChanges();
        }
      });
    } else {
      this.employeeService.createDesignation(payload).subscribe({
        next: (created) => {
          this.designations.push(created);
          this.reset();
          this.cdr.detectChanges();
        },
        error: () => {
          this.loading = false;
          this.error = 'Failed to create designation';
          this.cdr.detectChanges();
        }
      });
    }
  }

  reset() {
    this.loading = false;
    this.showForm = false;
    this.editingId = null;
    this.designationForm.reset();
  }
}
