import { api } from '../api.service';


export async function Login_service(username: string, password: string) {

    const res = await api.post("/login", { username, password });
    return {
        status: res.status,
        data: res.data
    }

}