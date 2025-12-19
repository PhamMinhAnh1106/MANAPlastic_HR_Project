
import { api } from "../../../api.service";
//  chu ky luong
export interface ChuKyLuongItem {
    id: number;
    month: number;
    year: number;
    cycleStartDate: string;   // ISO date: "YYYY-MM-DD"
    cycleEndDate: string;     // ISO date: "YYYY-MM-DD"
    standardWorkDays: number;
}

export async function getChukiluong(year: string) {
    try {
        const res = await api.get(`/legalsetting/monthlyConfig?year=${year}`);
        return res.data;
    } catch (error) {
        return error;
    }
}
export interface chukiluong {
    year?: string;
    standardWorkDays?: number;
    cycleStartDay?: number;
    isCyclePreviousMonth?: boolean;
}
export async function postChukiluong(form: chukiluong) {
    try {
        const res = await api.post("/legalsetting/monthlyConfig/generateYear", form);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}
export async function putChukiluong(id: number, form: chukiluong) {
    try {
        const res = await api.put(`/legalsetting/monthlyConfig/${id}`, form);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}

// leavePolicies

export interface LeaveTypeDetail {
    id: number;
    shiftname: string;
    starttime: string;        // "HH:mm:ss"
    endtime: string;          // "HH:mm:ss"
    durationHours: number;
    shiftnameAsEnum: string;  // ví dụ: "ANNUAL"
}

export interface LeavePolicy {
    id: number;
    days: number;
    description: string;
    leavetype: string;        // "ANNUAL"
    minyearsservice: number;
    maxyearsservice: number;
    gendertarget: string | null;
    jobtype: string | null;
    leavetypeid: LeaveTypeDetail;
}

export async function getleavePolicies() {
    try {
        const res = await api.get(`/legalsetting/leavePolicies`);
        return res.data;
    } catch (error) {
        return error;
    }
}


export interface PostLeavePolicy {
    leaveType?: string;
    minYearsService?: number;
    maxYearsService?: number | null;
    days?: number;
    genderTarget?: string | null;
    description?: string;
    leaveTypeId?: number;
}

export async function postleavePolicies(form: PostLeavePolicy) {
    try {
        const res = await api.post("/legalsetting/leavePolicies", form);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}


export async function putleavePolicies(id: number, form: PostLeavePolicy) {
    try {
        const res = await api.put(`/legalsetting/leavePolicies/${id}`, form);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}


export async function DeleteleavePolicies(id: number) {
    try {
        const res = await api.delete(`/legalsetting/leavePolicies/${id}`);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}


//over time 

export interface OvertimeType {
    id: number;
    otCode: string;
    otName: string;
    rate: number;
    isTaxExemptPart: boolean;
    calculationType: 'MULTIPLIER' | 'FIXED' | 'EXCESS_ONLY' | string;
    taxExemptFormula: 'EXCESS_ONLY' | 'FULL' | 'NONE' | string;
    taxExemptPercentage: number;
    description: string;
}

export async function getOvertime() {
    try {
        const res = await api.get(`/legalsetting/overtimeTypes`);
        return res.data;
    } catch (error) {
        return error;
    }
}

export interface OvertimeCreateRequest {
    otCode?: string;
    otName?: string;
    rate?: number;
    calculationType?: 'MULTIPLIER' | 'FIXED' | string;
    isTaxExemptPart?: boolean;
    taxExemptFormula?: 'EXCESS_ONLY' | 'FULL' | 'NONE' | string;
    description?: string;
}

export async function postOvertime(form: OvertimeCreateRequest) {
    try {
        const res = await api.post("/legalsetting/overtimeTypes", form);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}


export async function putOvertime(id: number, form: OvertimeCreateRequest) {
    try {
        const res = await api.put(`/legalsetting/overtimeTypes/${id}`, form);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}


export async function DeleteOvertime(id: number) {
    try {
        const res = await api.delete(`/legalsetting/overtimeTypes/${id}`);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}

// TAX

export interface SystemSetting {
    id: number;
    settingKey: string;
    value: number;
    effectiveDate: string; // ISO date: 'YYYY-MM-DD'
    isActive: boolean;
    description: string;
}


export async function getTaxQuery(query: string) {
    try {
        const res = await api.get(`/legalsetting/taxSettings?keyword=${query}`);
        return res.data;
    } catch (error) {
        return error;
    }

}
export async function getTaxAll() {
    try {
        const res = await api.get("/legalsetting/taxSettings/current");
        return res.data;
    } catch (error) {
        return error;
    }
}
export interface SystemSettingCreateRequest {
    settingKey?: string;
    value?: number;
    effectiveDate?: string;
    description?: string;
    isActive?: boolean;
}

export async function postTax(form: SystemSettingCreateRequest) {
    try {
        const res = await api.post("/legalsetting/taxSettings", form);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}


export async function putTax(id: number, form: SystemSettingCreateRequest) {
    try {
        const res = await api.put(`/legalsetting/taxSettings/${id}`, form);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}