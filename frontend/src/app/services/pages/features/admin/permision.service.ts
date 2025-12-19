import { api } from "../../../api.service";

export async function Getpermission(username: string, page: number, size: number, query: string) {
    try {
        const res = await api.get(`/admin/permissions/user/${username}?page=${page}&size=${size}&${query}`);
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

export async function GetpermissionRole(id: number, page: number, size: number) {
    try {
        const res = await api.get(`/admin/rolePermissions/${id}?page=${page}&size=${size}`);
        return res.data;
    } catch (error) {
        return error;
    }
}

export interface putPermissionRole {
    roleId: number,
    permissionId: number,
    active: boolean
}


export async function putPermissionRole(forms: putPermissionRole) {
    try {
        const res = await api.put("/admin/rolePermissions/update", forms);
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return error;
    }
}