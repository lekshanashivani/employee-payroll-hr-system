import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { Bonus, GeneratePayslipRequest, Payslip } from '../models/payroll.model';

@Injectable({
    providedIn: 'root'
})
export class PayrollService {

    constructor(private api: ApiService) { }

    // Payslips
    getMyPayslips(employeeId: number): Observable<Payslip[]> {
        return this.api.get<Payslip[]>(`/payroll/employee/${employeeId}`);
    }

    generatePayslip(request: GeneratePayslipRequest): Observable<Payslip> {
        return this.api.post<Payslip>('/payroll/payslips/generate', request);
    }

    getPayslipById(id: number): Observable<Payslip> {
        return this.api.get<Payslip>(`/payroll/payslips/${id}`);
    }

    // Bonuses
    grantBonus(bonus: Partial<Bonus>): Observable<Bonus> {
        return this.api.post<Bonus>('/payroll/bonuses', bonus);
    }
}
