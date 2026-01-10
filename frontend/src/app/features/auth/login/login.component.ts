import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    template: `
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <h2>Welcome Back</h2>
          <p>Sign in to HR Payroll System</p>
        </div>
        
        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label for="email">Email Address</label>
            <input 
              type="email" 
              id="email" 
              formControlName="email" 
              class="form-control"
              placeholder="admin@company.com"
            >
            <div class="error-feedback" *ngIf="submitted && f['email'].errors">
              <span *ngIf="f['email'].errors['required']">Email is required</span>
              <span *ngIf="f['email'].errors['email']">Invalid email format</span>
            </div>
          </div>

          <div class="form-group">
            <label for="password">Password</label>
            <input 
              type="password" 
              id="password" 
              formControlName="password" 
              class="form-control"
              placeholder="••••••••"
            >
            <div class="error-feedback" *ngIf="submitted && f['password'].errors">
              <span *ngIf="f['password'].errors['required']">Password is required</span>
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" [disabled]="loading">
              <span *ngIf="!loading">Sign In</span>
              <span *ngIf="loading">Signing in...</span>
            </button>
          </div>

          <div class="error-message" *ngIf="error">
            {{ error }}
          </div>
        </form>
      </div>
    </div>
  `,
    styles: [`
    .login-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background-color: var(--bg-secondary);
    }

    .login-card {
      background: var(--bg-primary);
      padding: 2.5rem;
      border-radius: 12px;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
      width: 100%;
      max-width: 400px;
      border: 1px solid var(--border-color);
    }

    .login-header {
      text-align: center;
      margin-bottom: 2rem;
    }

    .login-header h2 {
      margin: 0;
      color: var(--text-primary);
      font-size: 1.5rem;
      font-weight: 600;
    }

    .login-header p {
      margin: 0.5rem 0 0;
      color: var(--text-secondary);
      font-size: 0.875rem;
    }

    .form-group {
      margin-bottom: 1.25rem;
    }

    label {
      display: block;
      margin-bottom: 0.5rem;
      color: var(--text-primary);
      font-weight: 500;
      font-size: 0.875rem;
    }

    .form-control {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid var(--border-color);
      border-radius: 6px;
      background: var(--bg-secondary);
      color: var(--text-primary);
      font-size: 0.875rem;
      transition: border-color 0.2s;
    }

    .form-control:focus {
      outline: none;
      border-color: var(--primary-color);
      box-shadow: 0 0 0 3px var(--primary-color-dim);
    }

    button {
      width: 100%;
      padding: 0.75rem;
      background: var(--primary-color);
      color: white;
      border: none;
      border-radius: 6px;
      font-weight: 600;
      cursor: pointer;
      transition: background-color 0.2s;
    }

    button:hover:not(:disabled) {
      background: var(--primary-hover);
    }

    button:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }

    .error-feedback {
      color: var(--error-color);
      font-size: 0.75rem;
      margin-top: 0.25rem;
    }

    .error-message {
      padding: 0.75rem;
      background: var(--error-bg);
      color: var(--error-color);
      border-radius: 6px;
      margin-top: 1rem;
      font-size: 0.875rem;
      text-align: center;
    }
  `]
})
export class LoginComponent {
    loginForm: FormGroup;
    loading = false;
    submitted = false;
    error = '';

    constructor(
        private formBuilder: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {
        if (this.authService.isAuthenticated()) {
            this.router.navigate(['/dashboard']);
        }

        this.loginForm = this.formBuilder.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', Validators.required]
        });
    }

    get f() { return this.loginForm.controls; }

    onSubmit() {
        this.submitted = true;
        this.error = '';

        if (this.loginForm.invalid) {
            return;
        }

        this.loading = true;
        this.authService.login(this.loginForm.value).subscribe({
            next: () => {
                this.router.navigate(['/dashboard']);
            },
            error: error => {
                this.error = 'Invalid email or password';
                this.loading = false;
            }
        });
    }
}
