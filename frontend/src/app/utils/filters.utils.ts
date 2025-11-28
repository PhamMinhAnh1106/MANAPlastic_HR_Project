import { api } from "../services/api.service";

export async function FilterUser(query: string, role: string) {
    try {
        const res = await api.get(`/${role.toLowerCase()}/userFilter?${query}`);
        return res.data;
    } catch (error) {
        return "co loi xay ra" + error;
    }
}
export function buildQueryParams(filter: any): string {
    const params = [];

    for (const key in filter) {
        if (filter[key] !== "") {
            params.push(`${key}=${encodeURIComponent(filter[key])}`);
        }
    }

    return params.join("&");
}