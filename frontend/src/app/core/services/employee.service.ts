import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { CreateEmployeeRequest, Designation, Employee } from '../models/employee.model';

@Injectable({
    providedIn: 'root'
})
export class EmployeeService {

    constructor(private api: ApiService) { }

    // Employee Endpoints
    getAllEmployees(): Observable<Employee[]> {
        return this.api.get<Employee[]>('/employees');
    }

    getEmployeeById(id: number): Observable<Employee> {
        return this.api.get<Employee>(`/employees/${id}`);
    }

    createEmployee(request: CreateEmployeeRequest): Observable<Employee> {
        return this.api.post<Employee>('/employees', request);
    }

    updateEmployee(id: number, employee: Partial<Employee>): Observable<Employee> {
        return this.api.put<Employee>(`/employees/${id}`, employee);
    }

    // Designation Endpoints
    getAllDesignations(): Observable<Designation[]> {
        return this.api.get<Designation[]>('/employees/designations');
    }

    createDesignation(designation: Partial<Designation>): Observable<Designation> {
        return this.api.post<Designation>('/employees/designations', designation);
    }

    updateDesignation(id: number, designation: Partial<Designation>): Observable<Designation> {
        return this.api.put<Designation>(`/employees/designations/${id}`, designation);
    }

    deleteDesignation(id: number): Observable<void> {
        return this.api.delete<void>(`/employees/designations/${id}`);
    }
}
