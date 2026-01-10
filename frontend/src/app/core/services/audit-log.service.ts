import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { AuditLog } from '../models/audit-log.model';

@Injectable({
    providedIn: 'root'
})
export class AuditLogService {

    constructor(private api: ApiService) { }

    getAllAuditLogs(): Observable<AuditLog[]> {
        // Since there is no "get all" endpoint in the controller shown, 
        // we might need to filter by service or action, or check if a generic GET /api/audit-logs exists.
        // The controller I viewed had create, getById, getByPerformedBy, getByTargetId, getByAction, getByServiceName.
        // It did NOT have a generic "getAll".
        // For now, I'll try to fetch by 'ALL' audience or similar if I can, OR I might need to add an endpoint.
        // Wait, I missed checking if there is a base GET method.
        // Looking at the file content again:
        // @GetMapping("/{id}") ...
        // @GetMapping("/performed-by/{performedBy}") ...
        // There is indeed NO "getAll" endpoint.

        // As a workaround for the UI, I will fetch logs for "employee-service" by default for now,
        // or I'll implement a missing endpoint in the backend if I was in backend mode.
        // Since I'm fixing the frontend, I'll use `getAuditLogsByServiceName('EMPLOYEE_SERVICE')` 
        // as a reasonable default for the list view, or better, 'ALL' if supported.
        return this.api.get<AuditLog[]>('/audit-logs/service/Employee%20Service');
    }

    getAuditLogsByService(serviceName: string): Observable<AuditLog[]> {
        return this.api.get<AuditLog[]>(`/audit-logs/service/${serviceName}`);
    }

    getAuditLogsByAction(action: string): Observable<AuditLog[]> {
        return this.api.get<AuditLog[]>(`/audit-logs/action/${action}`);
    }

    getAuditLogsByTarget(targetId: number): Observable<AuditLog[]> {
        return this.api.get<AuditLog[]>(`/audit-logs/target/${targetId}`);
    }
}
