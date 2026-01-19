import { api } from "../../../api.service";

export interface OverTimeDetail {
    overtimeTypeName: string,
    hours: number,
    startTime: string,
    endTime: string
}
export interface OverTimeRequest {
    requestId: number,
    date: string,
    startTime: string,
    endTime: string,
    totalHours: number,
    finalPaidHours: number,
    status: string,
    reason: string,
    isSystemGenerated: boolean,
    employeeId: string,
    employeeName: string,
    departmentName: string,
    managerName: string,
    hrName: string,
    createdAt: string,
    updatedAt: string,
    details: OverTimeDetail[]
}
export async function GetOverTimeRequest(query: string) {
    try {
        const res = await api.get(`/user/overTimeRequest?${query}`);
        return res.data;
    } catch (error) {
        return error;
    }

}

export interface CreateOverTimeRequestI {
    date: string,
    startTime: string,
    endTime: string,
    reason: string,
    overtimetypeid: number
}
export async function CreateOverTimeRequest(data: CreateOverTimeRequestI) {
    try {
        const res = await api.post("/user/overTimeRequest", data);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}



export async function ScanDailyOt(date: string) {
    try {
        const res = await api.post(`/user/overTimeRequest/scanDailyOt?date=${date}`);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}

export interface DetailsApprove {
    id: number,
    hours: number,
    overtimeTypeID: number,
    startTime: string,
    endTime: string
}
export interface ApproveOverTimeRequestI {
    note: string,
    details: DetailsApprove[]
}
export async function ApproveOverTimeRequest(body: ApproveOverTimeRequestI, requestId: number, role: string) {
    try {
        const res = await api.put(`/user/overTimeRequest/${role}/approve/${requestId}`, body);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }

}


export interface RejectRequest {
    reason: string
}
export async function RejectOverTimeRequest(requestId: number, body: RejectRequest) {
    try {
        const res = await api.put(`/user/overTimeRequest/reject/${requestId}`, body);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }

}