import axios from "axios";
import { refreshAccessToken } from "../utils/token.utils";
const url = "http://localhost:8080";
export const api = axios.create({
    baseURL: url,
    withCredentials: true,
    // headers: {
    //     "Content-Type": "application/json"
    // }
})

api.interceptors.request.use((config) => {
    const token = getCookie('access_token');
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
})
let isRefreshing = false;
let refreshPromise: Promise<string> | null = null;

api.interceptors.response.use(
    response => response,
    async error => {
        const originalRequest = error.config;

        if (
            error.response?.status === 400 &&
            !originalRequest._retry &&
            !originalRequest.url.includes('/refresh')
        ) {
            originalRequest._retry = true;

            if (!isRefreshing) {
                isRefreshing = true;
                const refreshToken = getCookie('refreshToken');

                if (!refreshToken) {

                    return Promise.reject(error);
                }

                refreshPromise = refreshAccessToken(refreshToken).then(res => {
                    document.cookie = `access_token=${res.token}; path=/`;
                    document.cookie = `refreshToken=${res.refreshToken}; path=/`;
                    return res.token;
                });

            }

            const newToken = await refreshPromise;
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            return api(originalRequest);
        }

        return Promise.reject(error);
    }
);


function getCookie(name: string): string | null {
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    if (match) return match[2];
    return null;
}


