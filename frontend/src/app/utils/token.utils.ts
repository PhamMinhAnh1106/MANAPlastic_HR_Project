import { jwtDecode } from 'jwt-decode';

interface token_res {
    "roles": string[]
    "sub": string,
}

export function DecodeTokenRole(token: string) {
    const payload = jwtDecode<token_res>(token);
    return payload.roles;
}