export interface PayslipHeader {
    username: string;
    fullname: string;
    departmentname: string;
    // Data trả về là basesalarysnapshot, không phải basesalary
    basesalarysnapshot: number;
    insurancesalarysnapshot?: number; // Thêm từ data mẫu
    jobtype: string;
    actualworkdays: number;
    standardworkdays: number; // Thêm từ data mẫu
    payperiod: string; // "YYYY-MM"
    userID: string | number;
    totalincome: number;
    dependentcount?: number;

    // Các trường snapshot header (để fallback nếu cần, dù data đã có mảng riêng)
    bhxh_comp?: number;
    bhyt_comp?: number;
    bhtn_comp?: number;
    pit?: number;
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
    company_contributions: PayslipItem[]; // Thêm mảng này từ data
    net_salary: number;
    explanations?: any[];
}

export interface AuditUser {
    userID: number | string;
    username: string;
    fullname: string;
}