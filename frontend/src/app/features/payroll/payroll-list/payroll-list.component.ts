import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { PayrollService } from '../../../core/services/payroll.service';
import { Payslip } from '../../../core/models/payroll.model';

@Component({
    selector: 'app-payroll-list',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    template: `
    <div class="page-header">
      <h2>Payslips</h2>
      
      <!-- Generate Section (HR/Admin only) -->
      <div *ngIf="canGenerate" class="generate-box">
         <form [formGroup]="generateForm" (ngSubmit)="onGenerate()" class="inline-form">
           <input type="month" formControlName="payPeriod" class="form-control">
           <input type="number" formControlName="employeeId" placeholder="Emp ID" class="form-control" style="width: 100px;">
           <button type="submit" [disabled]="generating" class="btn-primary">
             {{ generating ? 'Generating...' : 'Generate New' }}
           </button>
         </form>
         <span *ngIf="error" class="error-msg">{{ error }}</span>
      </div>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>Pay Period</th>
            <th>Base Salary</th>
            <th>Bonuses</th>
            <th>Deductions</th>
            <th>Tax</th>
            <th>Net Salary</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let slip of payslips">
            <td class="period-cell">{{ slip.payPeriod }}</td>
            <td>{{ slip.baseSalary | currency }}</td>
            <td class="positive">+{{ slip.totalBonuses | currency }}</td>
            <td class="negative">-{{ slip.unpaidLeaveDeduction | currency }}</td>
            <td class="negative">-{{ slip.taxAmount | currency }}</td>
            <td class="net-salary">{{ slip.netSalary | currency }}</td>
            <td>
              <button class="btn-link">View PDF</button>
            </td>
          </tr>
          <tr *ngIf="payslips.length === 0">
            <td colspan="7" class="empty-state">No payslips found</td>
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

    .generate-box {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
    }

    .inline-form {
      display: flex;
      gap: 0.5rem;
    }

    .form-control {
      padding: 0.5rem;
      border: 1px solid var(--border-color);
      border-radius: 4px;
    }

    .btn-primary {
      background: var(--primary-color);
      color: white;
      border: none;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      cursor: pointer;
    }

    .table-container {
      background: var(--surface-color);
      border-radius: 8px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
      overflow: hidden;
    }

    .data-table { width: 100%; border-collapse: collapse; }
    
    .data-table th, .data-table td {
      padding: 1rem;
      text-align: right; /* Financial data alignment */
      border-bottom: 1px solid var(--border-color);
    }

    .data-table th:first-child, .data-table td:first-child { text-align: left; }
    .data-table th:last-child, .data-table td:last-child { text-align: center; }

    .period-cell { font-weight: 600; color: var(--text-primary); }
    .positive { color: var(--success-color); }
    .negative { color: var(--error-color); }
    .net-salary { font-weight: 700; color: var(--primary-color); }

    .btn-link {
      background: none;
      border: none;
      color: var(--primary-color);
      cursor: pointer;
      text-decoration: underline;
    }

    .error-msg { color: var(--error-color); font-size: 0.75rem; margin-top: 0.25rem; }
  `]
})
export class PayrollListComponent implements OnInit {
    payslips: Payslip[] = [];
    generateForm: FormGroup;
    canGenerate = false;
    generating = false;
    error = '';
    currentUserId: number = 0;

    constructor(
        private payrollService: PayrollService,
        private authService: AuthService,
        private fb: FormBuilder
    ) {
        this.generateForm = this.fb.group({
            payPeriod: ['', Validators.required],
            employeeId: ['', Validators.required]
        });
    }

    ngOnInit() {
        const user = this.authService.currentUser();
        if (user) {
            this.currentUserId = user.userId;
            this.canGenerate = ['HR', 'ADMIN'].includes(user.role);

            // If employee, load their payslips. Admin loads all later (simplified for now)
            this.loadPayslips();
        }
    }

    loadPayslips() {
        this.payrollService.getMyPayslips(this.currentUserId).subscribe({
            next: (data) => this.payslips = data
        });
    }

    onGenerate() {
        if (this.generateForm.invalid) return;

        this.generating = true;
        this.error = '';

        const req = {
            employeeId: this.generateForm.value.employeeId,
            payPeriod: this.generateForm.value.payPeriod
        };

        this.payrollService.generatePayslip(req).subscribe({
            next: (slip) => {
                this.generating = false;
                // Refresh list if we generated for ourselves or viewing all
                if (req.employeeId == this.currentUserId) {
                    this.payslips.unshift(slip);
                }
                alert('Payslip Generated Successfully!');
            },
            error: (err) => {
                this.generating = false;
                this.error = 'Failed to generate. Check if already exists.';
            }
        });
    }
}
