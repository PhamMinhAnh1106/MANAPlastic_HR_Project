import { api } from "../../../api.service";

export interface RewaPunis {
    rewaid: number
    userID: number;
    userName?: string;
    type: string;
    reason: string;
    decisionDate: string;
    amount: number;
    isTaxExempt: boolean;
    status: string;
}
export async function GetRewaPunis(params: string, page: number, size: number) {
    try {
        const res = await api.get(`/hr/rewaPunis?page=${page}&size=${size}&${params}`);
        return res.data;
    } catch (error) {
        return error;
    }
}

export async function DetailRewaPunis(param: number) {
    try {
        const res = await api.get(`/hr/rewaPunis/${param}`);
        return res.data;
    } catch (error) {
        return error;
    }
}

export async function AddRewaPunis(data: RewaPunis) {
    try {
        const res = await api.post(`/hr/rewaPunis`, data);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}

export async function UpdateRewaPunis(param: number, data: RewaPunis) {
    try {
        const res = await api.put(`/hr/rewaPunis/${param}`, data);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}
export async function DeleteRewaPunis(param: number) {
    try {
        const res = await api.delete(`/hr/rewaPunis/${param}`);
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}