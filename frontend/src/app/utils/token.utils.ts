import { jwtDecode } from 'jwt-decode';
import { api } from '../services/api.service';

interface token_res {
    "roles": string[]
    "sub": string,
}

export function DecodeTokenRole(token: string) {
    const payload = jwtDecode<token_res>(token);
    return payload.roles;
}
export async function refreshAccessToken(refreshtoken: string) {
    const res = await api.post("/refresh", { refreshToken: refreshtoken });
    return res.data;
}