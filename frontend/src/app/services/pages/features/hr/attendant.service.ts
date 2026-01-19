import { api } from "../../../api.service";


export async function GetAttendants(params: string, page: number, size: number) {
    try {

        const res = await api.get(`/chamCong?page=${page}&size=${size}&${params}`);
        return res.data;
    } catch (error) {
        return error;
    }
}

export async function DeleteAttendant(param: number) {
    try {
        const res = await api.delete(`/hr/chamCong/${param}`);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}