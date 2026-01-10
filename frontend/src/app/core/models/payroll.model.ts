export interface Payslip {
    id: number;
    employeeId: number;
    payPeriod: string; // YYYY-MM
    baseSalary: number;
    taxPercentage: number;
    totalBonuses: number;
    unpaidLeaveDeduction: number;
    unpaidLeaveDays: number;
    taxAmount: number;
    netSalary: number;
    generatedBy: number;
    generatedAt: string;
}

export interface Bonus {
    id: number;
    employeeId: number;
    amount: number;
    reason: string;
    payPeriod: string;
    grantedAt: string;
}

export interface GeneratePayslipRequest {
    employeeId: number;
    payPeriod: string;
}
