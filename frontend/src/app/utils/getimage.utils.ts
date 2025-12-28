import { api } from "../services/api.service";

export async function getImageContracts(name: string) {
    try {
        const res = await api.get(`/hr/contracts/files${name}`, {
            responseType: 'blob',
        });
        return res.data;
    } catch (error) {
        return error;
    }
}

export async function getImageChamcong(name: string) {
    try {
        const res = await api.get(`/chamCong/images${name}`, {
            responseType: 'blob',
        });
        return res.data;
    } catch (error) {
        return error;
    }
}
export async function getImageAttendance(name: string) {
    try {
        const res = await api.get(`/user/attendanceRequests/images${name}`, {
            responseType: 'blob',
        });
        return res.data;
    } catch (error) {
        return error;
    }
}
