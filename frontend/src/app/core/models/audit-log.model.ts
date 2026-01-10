export interface AuditLog {
    id: number;
    action: string;
    serviceName: string;
    performedBy: number;
    targetId: number;
    description: string;
    oldValues?: Record<string, any>;
    newValues?: Record<string, any>;
    createdAt: string; // Assuming standard timestamp field
}

export interface AuditLogRequest {
    action: string;
    serviceName: string;
    performedBy: number;
    targetId: number;
    description: string;
    oldValues?: Record<string, any>;
    newValues?: Record<string, any>;
}
