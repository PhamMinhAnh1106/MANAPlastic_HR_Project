import axios from "axios";
const url = "http://localhost:8080";
export const api = axios.create({
    baseURL: url,
    withCredentials: true,
    headers: {
        "Content-Type": "application/json"
    }
})

api.interceptors.request.use((config) => {
    const token = getCookie('access_token');
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
})

function getCookie(name: string): string | null {
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    if (match) return match[2];
    return null;
}


