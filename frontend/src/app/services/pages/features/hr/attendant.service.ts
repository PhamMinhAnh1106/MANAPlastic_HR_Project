import { api } from "../../../api.service";


export async function GetAttendants(params: string) {
    try {
        const res = await api.get(`/hr/chamCong?${params}`);
        return res.data;
    } catch (error) {
        return "co loi xay ra " + error;
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
        return "co loi xay ra " + error;
    }
}