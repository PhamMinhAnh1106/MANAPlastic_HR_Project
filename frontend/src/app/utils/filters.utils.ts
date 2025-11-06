import { api } from "../services/api.service";

export async function FilterUser(query: string, role: string) {
    try {
        const res = await api.get(`/${role.toLowerCase()}/userFilter?${query}`);
        return res.data;
    } catch (error) {
        return "co loi xay ra" + error;
    }
}