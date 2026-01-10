import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService, Notification, Announcement } from '../../../core/services/notification.service';

@Component({
    selector: 'app-notifications',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="notifications-container">
      <div class="card announcements">
        <h3>📢 Announcements</h3>
        <div *ngFor="let ann of announcements" class="announcement-item">
          <h4>{{ ann.title }}</h4>
          <p>{{ ann.message }}</p>
          <span class="access-date">{{ ann.createdAt | date:'short' }}</span>
        </div>
        <div *ngIf="announcements.length === 0" class="empty">No active announcements</div>
      </div>

      <div class="card alerts">
        <h3>🔔 My Notifications</h3>
        <div *ngFor="let notif of notifications" class="notification-item">
          <p>{{ notif.message }}</p>
          <span class="time">{{ notif.createdAt | date:'shortTime' }}</span>
        </div>
        <div *ngIf="notifications.length === 0" class="empty">No new notifications</div>
      </div>
    </div>
  `,
    styles: [`
    .notifications-container {
      display: grid;
      grid-template-columns: 1fr;
      gap: 1.5rem;
    }
    
    .card {
      background: var(--surface-color);
      padding: 1.5rem;
      border-radius: 8px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    }

    h3 { margin-top: 0; margin-bottom: 1rem; color: var(--text-primary); }

    .announcement-item {
      background: #f1f5f9;
      padding: 1rem;
      border-radius: 6px;
      margin-bottom: 0.75rem;
    }

    .announcement-item h4 { margin: 0 0 0.25rem 0; color: var(--primary-color); }
    .announcement-item p { margin: 0; font-size: 0.875rem; color: var(--text-primary); }

    .notification-item {
      border-bottom: 1px solid var(--border-color);
      padding: 0.75rem 0;
    }
    .notification-item p { margin: 0; font-size: 0.875rem; }
    .time, .access-date { font-size: 0.75rem; color: var(--text-secondary); display: block; margin-top: 0.25rem; }

    .empty { color: var(--text-secondary); font-style: italic; }
  `]
})
export class NotificationListComponent implements OnInit {
    notifications: Notification[] = [];
    announcements: Announcement[] = [];

    constructor(
        private notifService: NotificationService,
        private authService: AuthService
    ) { }

    ngOnInit() {
        const user = this.authService.currentUser();
        if (user?.userId) {
            // Load both in parallel
            this.notifService.getMyNotifications(user.userId).subscribe(
                data => this.notifications = data
            );
            this.notifService.getActiveAnnouncements().subscribe(
                data => this.announcements = data
            );
        }
    }
}
