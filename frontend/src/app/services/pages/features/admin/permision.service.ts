import { api } from "../../../api.service";

export async function Getpermission(username: string, page: number, size: number) {
    try {
        const res = await api.get(`/admin/permissions/user/${username}?page=${page}&size=${size}`);
        return res.data.content;
    } catch (error) {
        return error;
    }
}
interface changePer {
    username: string,
    permissionId: number,
    activePermission: number
}
export async function Changepermission(form: changePer) {
    try {
        const res = await api.post("/admin/permissions/update", form);
        return res.data;
    } catch (error) {
        return error;
    }
}
export async function Deletepermission(permissionId: number, username: string) {
    try {
        const res = await api.delete(`/admin/permissions/reset/${permissionId}?username=${username}`);
        return res.data;
    } catch (error) {
        return error;
    }
}