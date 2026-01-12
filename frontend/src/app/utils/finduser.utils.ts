import { api } from "../services/api.service";


export async function findUserbyUsername(username: string) {
    try {
        const res = await api.get(`/manager/searchUsers?keyword=${username}`);
        return res.data;
    } catch (error) {
        return error;
    }

}