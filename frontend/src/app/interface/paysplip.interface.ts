export interface PayslipHeader {
    username: string;
    fullname: string;
    departmentname: string;
    basesalary: number;
    jobtype: string;
    actualworkdays: number;
    payperiod: string; // "YYYY-MM"
    userID: string | number;
    totalincome: number;
    bhxh_comp?: number;
    bhyt_comp?: number;
    bhtn_comp?: number;
}

export interface PayslipItem {
    rule_code: string;
    name: string;
    value: number;
}

export interface PayslipResponse {
    status: 'FINAL' | 'DRAFT' | 'PENDING';
    header: PayslipHeader;
    incomes: PayslipItem[];
    deductions: PayslipItem[];
    net_salary: number;
}

export interface AuditUser {
    userID: number | string; // userId
    username: string;
    fullname: string;
}