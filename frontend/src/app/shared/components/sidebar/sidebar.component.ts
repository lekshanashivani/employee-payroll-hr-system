import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <aside class="sidebar">
      <div class="logo">
        <h1>HR Payroll</h1>
      </div>
      
      <nav class="nav-links">
        <a routerLink="/dashboard" routerLinkActive="active" class="nav-item">
          <span class="icon">📊</span>
          Dashboard
        </a>

        <!-- Common Links -->
        <a routerLink="/attendance" routerLinkActive="active" class="nav-item">
          <span class="icon">📅</span>
          Attendance
        </a>
        
        <a routerLink="/payroll" routerLinkActive="active" class="nav-item">
          <span class="icon">💰</span>
          Payroll
        </a>

        <a routerLink="/leave-requests" routerLinkActive="active" class="nav-item">
          <span class="icon">📝</span>
          Leave Requests
        </a>

        <a routerLink="/meeting-requests" routerLinkActive="active" class="nav-item" *ngIf="!isAdmin()">
          <span class="icon">💬</span>
          HR Meetings
        </a>

        <a routerLink="/notifications" routerLinkActive="active" class="nav-item">
            <span class="icon">🔔</span>
            Notifications
        </a>

        <!-- Admin Only Management Links -->
        <ng-container *ngIf="canManageEmployees()">
          <a routerLink="/employees" routerLinkActive="active" class="nav-item">
            <span class="icon">👥</span>
            Employees
          </a>
        </ng-container>

        <ng-container *ngIf="isAdmin()">
          <a routerLink="/designations" routerLinkActive="active" class="nav-item">
            <span class="icon">🏷️</span>
            Designations
          </a>
          
          <a routerLink="/audit-logs" routerLinkActive="active" class="nav-item">
            <span class="icon">🛡️</span>
            Audit Logs
          </a>
        </ng-container>

        <!-- Employee Links -->
        <ng-container *ngIf="!isAdmin()">
            <a [routerLink]="['/employees', getEmployeeId()]" routerLinkActive="active" class="nav-item" *ngIf="getEmployeeId()">
                <span class="icon">👤</span>
                My Profile
            </a>
        </ng-container>
      </nav>

      <div class="user-info">
        <p>{{ getUserEmail() }}</p>
        <button (click)="logout()" class="logout-btn">Logout</button>
      </div>
    </aside>
  `,
  styles: [`
    .sidebar {
      width: 250px;
      height: 100vh;
      background: var(--primary-color);
      color: white;
      display: flex;
      flex-direction: column;
      position: fixed;
      left: 0;
      top: 0;
    }

    .logo {
      padding: 1.5rem;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    }

    .logo h1 {
      margin: 0;
      font-size: 1.25rem;
      font-weight: 700;
    }

    .nav-links {
      flex: 1;
      padding: 1rem 0;
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .nav-item {
      display: flex;
      align-items: center;
      padding: 0.75rem 1.5rem;
      color: rgba(255, 255, 255, 0.8);
      text-decoration: none;
      transition: all 0.2s;
    }

    .nav-item:hover, .nav-item.active {
      background: rgba(255, 255, 255, 0.1);
      color: white;
      border-right: 3px solid white;
    }

    .icon {
      margin-right: 0.75rem;
    }

    .user-info {
      padding: 1.5rem;
      border-top: 1px solid rgba(255, 255, 255, 0.1);
      background: rgba(0, 0, 0, 0.1);
    }

    .user-info p {
      margin: 0 0 0.5rem 0;
      font-size: 0.875rem;
      opacity: 0.9;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .logout-btn {
      width: 100%;
      padding: 0.5rem;
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.2);
      color: white;
      border-radius: 4px;
      font-size: 0.875rem;
      cursor: pointer;
    }

    .logout-btn:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  `]
})
export class SidebarComponent {
  constructor(private authService: AuthService) { }

  getUserEmail(): string {
    return this.authService.currentUser()?.email || '';
  }

  isAdmin(): boolean {
    return this.authService.currentUser()?.role === 'ADMIN';
  }

  isHr(): boolean {
    return this.authService.currentUser()?.role === 'HR';
  }

  canManageEmployees(): boolean {
    return this.isAdmin() || this.isHr();
  }

  getEmployeeId(): number | undefined {
    return this.authService.currentUser()?.employeeId;
  }

  logout() {
    this.authService.logout();
  }
}
