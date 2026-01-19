import { api } from "../../../api.service";

export interface AttendanceRequest {
    requestId: number;
    userId: number;
    employeeName: string;
    departmentName: string;
    date: string; // YYYY-MM-DD
    shiftName: string;
    requestType: 'CHECK_IN' | 'CHECK_OUT';
    checkInTime: string; // ISO datetime
    checkOutTime: string | null;
    imgProof: string;
    reason: string;
    status: 'PENDING' | 'APPROVED' | 'REJECTED';
    approverName: string | null;
    comment: string | null;
    createdAt: string; // ISO datetime
}

export async function GetDataAttenden(page: number, size: number) {
    try {
        const res = await api.get<AttendanceRequest[]>(`/user/attendanceRequests?page=${page}&size=${size}`);
        return res.data;
    } catch (error) {
        return error;
    }
}

export async function ApproveAttendanceRequest(requestId: number) {
    try {
        const res = await api.put(`/user/attendanceRequests/${requestId}/approve`);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}
export async function RejectAttendanceRequest(requestId: number, comment: string) {
    try {
        const res = await api.put(`/user/attendanceRequests/${requestId}/reject?comment=${comment}`);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}


export interface AttendanceRequestPayload {
    date: string;
    requestType: string;
    shiftId: string | number;
    checkInTime: string;
    reason: string;
    file: File;
}

export async function AddNewAttendanceRequests(data: AttendanceRequestPayload) {
    try {
        const formData = new FormData();

        formData.append('date', data.date);
        formData.append('requestType', data.requestType);
        formData.append('shiftId', data.shiftId.toString());
        formData.append('checkInTime', data.checkInTime);
        formData.append('reason', data.reason);

        formData.append('file', data.file);

        const res = await api.post('/user/attendanceRequests', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });

        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}