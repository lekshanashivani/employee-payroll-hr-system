export interface Designation {
    id: number;
    name: string;
    baseSalary: number;
    taxPercentage: number;
    bonusPercentage: number;
    active: boolean;
}

export interface Employee {
    id: number;
    userId: number;
    name: string;
    phoneNumber: string;
    dateOfBirth: string;
    address: string;
    department: string;
    designationId: number;
    designationName: string;
    status: 'ACTIVE' | 'INACTIVE';
    exitDate?: string;
}

export interface CreateEmployeeRequest {
    // User info
    email: string;
    password: string;
    role: 'EMPLOYEE' | 'HR' | 'ADMIN';

    // Employee info
    name: string;
    phoneNumber: string;
    dateOfBirth: string;
    address: string;
    department: string;
    designationId: number;
    status: 'ACTIVE';
}
