import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    {
        path: 'login',
        component: LoginComponent
    },
    {
        path: '',
        component: MainLayoutComponent,
        canActivate: [authGuard],
        children: [
            {
                path: 'dashboard',
                loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
            },
            {
                path: 'employees',
                loadComponent: () => import('./features/employees/employee-list/employee-list.component').then(m => m.EmployeeListComponent)
            },
            {
                path: 'designations',
                loadComponent: () => import('./features/employees/designation-list/designation-list.component').then(m => m.DesignationListComponent)
            },
            {
                path: 'employees/create',
                loadComponent: () => import('./features/employees/employee-create/employee-create.component').then(m => m.EmployeeCreateComponent)
            },
            {
                path: 'employees/:id',
                loadComponent: () => import('./features/employees/employee-detail/employee-detail.component').then(m => m.EmployeeDetailComponent)
            },
            {
                path: 'attendance',
                loadComponent: () => import('./features/attendance/attendance-list/attendance-list.component').then(m => m.AttendanceListComponent)
            },
            {
                path: 'leave-requests',
                loadComponent: () => import('./features/attendance/leave-request/leave-request.component').then(m => m.LeaveRequestComponent)
            },
            {
                path: 'payroll',
                loadComponent: () => import('./features/payroll/payroll-list/payroll-list.component').then(m => m.PayrollListComponent)
            },
            {
                path: 'notifications',
                loadComponent: () => import('./features/notifications/notification-list/notification-list.component').then(m => m.NotificationListComponent)
            },
            {
                path: 'audit-logs',
                loadComponent: () => import('./features/audit-logs/audit-log-list/audit-log-list.component').then(m => m.AuditLogListComponent)
            },
            {
                path: '',
                redirectTo: 'dashboard',
                pathMatch: 'full'
            }
        ]
    },
    {
        path: '**',
        redirectTo: '/login'
    }
];
