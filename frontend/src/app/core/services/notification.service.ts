import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { HttpHeaders } from '@angular/common/http';

export interface Notification {
    id: number;
    subject: string;
    body: string;
    type: string;
    status: string; // PENDING, SENT, FAILED
    createdAt: string;
}

export interface Announcement {
    id: number;
    title: string;
    content: string;
    createdAt: string;
}

@Injectable({
    providedIn: 'root'
})
export class NotificationService {

    constructor(private api: ApiService, private authService: AuthService) { }

    getMyNotifications(employeeId: number): Observable<Notification[]> {
        return this.api.get<Notification[]>(`/notifications/employee/${employeeId}`);
    }

    getActiveAnnouncements(): Observable<Announcement[]> {
        return this.api.get<Announcement[]>('/notifications/announcements');
    }

    createAnnouncement(announcement: any): Observable<Announcement> {
        const user = this.authService.currentUser();
        const headers = new HttpHeaders().set('X-User-Id', user?.userId?.toString() || '');
        return this.api.post<Announcement>('/notifications/announcements', announcement, { headers });
    }

    deleteAnnouncement(id: number): Observable<void> {
        return this.api.delete<void>(`/notifications/announcements/${id}`);
    }
}
