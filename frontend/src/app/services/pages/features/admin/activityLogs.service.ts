import { api } from "../../../api.service";

export interface ActivityLogs {

    id: number;
    action: string;
    actiontime: string; // ISO datetime
    userID: number | null;
    logType: 'INFO' | 'WARN' | 'ERROR' | string;
    details: string;
    username: string;

}

export async function ActivityLogsService(page: number, size: number, query: string) {
    try {
        const res = await api.get(`/system/activityLogs?page=${page}&size=${size}&${query}`);
        return res.data;
    } catch (error) {
        return error;
    }
}