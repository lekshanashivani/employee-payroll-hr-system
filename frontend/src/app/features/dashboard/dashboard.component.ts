import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="dashboard-header">
      <h1>Dashboard</h1>
      <p>Welcome back, {{ getUserName() }}</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <h3>Role</h3>
        <p class="value">{{ getUserRole() }}</p>
      </div>
      
      <!-- Placeholders for real stats -->
      <div class="stat-card">
        <h3>Date</h3>
        <p class="value">{{ today | date:'mediumDate' }}</p>
      </div>
    </div>
  `,
    styles: [`
    .dashboard-header {
      margin-bottom: 2rem;
    }

    .dashboard-header h1 {
      margin: 0;
      color: var(--text-primary);
    }

    .dashboard-header p {
      color: var(--text-secondary);
      margin-top: 0.5rem;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: 1.5rem;
    }

    .stat-card {
      background: var(--bg-primary);
      padding: 1.5rem;
      border-radius: 8px;
      border: 1px solid var(--border-color);
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }

    .stat-card h3 {
      margin: 0;
      font-size: 0.875rem;
      color: var(--text-secondary);
      font-weight: 500;
    }

    .stat-card .value {
      margin: 0.5rem 0 0 0;
      font-size: 1.5rem;
      font-weight: 600;
      color: var(--text-primary);
    }
  `]
})
export class DashboardComponent {
    today = new Date();

    constructor(private authService: AuthService) { }

    getUserName(): string {
        return this.authService.currentUser()?.email?.split('@')[0] || 'User';
    }

    getUserRole(): string {
        return this.authService.currentUser()?.role || 'EMPLOYEE';
    }
}
