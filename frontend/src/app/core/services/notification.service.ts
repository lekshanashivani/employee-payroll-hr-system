import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';

export interface Notification {
    id: number;
    message: string;
    type: string;
    isRead: boolean;
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

    constructor(private api: ApiService) { }

    getMyNotifications(employeeId: number): Observable<Notification[]> {
        return this.api.get<Notification[]>(`/notifications/employee/${employeeId}`);
    }

    getActiveAnnouncements(): Observable<Announcement[]> {
        return this.api.get<Announcement[]>('/notifications/announcements');
    }

    createAnnouncement(announcement: any): Observable<Announcement> {
        return this.api.post<Announcement>('/notifications/announcements', announcement);
    }

    deleteAnnouncement(id: number): Observable<void> {
        return this.api.delete<void>(`/notifications/announcements/${id}`);
    }
}
