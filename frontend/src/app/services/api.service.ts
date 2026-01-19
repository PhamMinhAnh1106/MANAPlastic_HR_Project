import axios from "axios";
import { refreshAccessToken } from "../utils/token.utils";

const url = "http://localhost:8080";
// const url = "https://ae217cd93c7e.ngrok-free.app";

export const api = axios.create({
    baseURL: url,
    withCredentials: true,
    // headers: {
    //     "Content-Type": "application/json"
    // }
})

// Request Interceptor
api.interceptors.request.use((config) => {
    const token = getCookie('access_token');
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
})

// Biến quản lý trạng thái refresh
let isRefreshing = false;
let refreshPromise: Promise<string> | null = null;

// Response Interceptor
api.interceptors.response.use(
    response => response,
    async error => {
        const originalRequest = error.config;
        const status = error.response?.status;
        const errorData = error.response?.data; // Lấy body lỗi

        // ĐIỀU KIỆN REFRESH TOKEN
        // 1. Kiểm tra mã lỗi: 401, 403 HOẶC (400 VÀ message là 'Access Denied')
        const isTokenError =
            status === 401 ||
            status === 403 ||
            (status === 400 && (errorData?.message === 'Access Denied' || errorData?.error === 'Bad Request'));
        // Lưu ý: Backend bạn trả về error="Bad Request" và message="Access Denied" khi hết hạn token.
        // Nên kiểm tra cả 2 hoặc 1 trong 2 tùy độ chính xác bạn muốn.

        if (
            isTokenError &&
            !originalRequest._retry &&            // Chưa từng thử lại request này
            !originalRequest.url.includes('/auth/refresh') // Tránh lặp vô hạn
        ) {
            originalRequest._retry = true;

            if (!isRefreshing) {
                isRefreshing = true;
                const refreshToken = getCookie('refreshToken');

                if (!refreshToken) {
                    return Promise.reject(error);
                }

                refreshPromise = refreshAccessToken(refreshToken)
                    .then(res => {

                        document.cookie = `access_token=${res.token}; path=/`;
                        document.cookie = `refreshToken=${res.refreshToken}; path=/`;

                        api.defaults.headers.common['Authorization'] = `Bearer ${res.token}`;
                        return res.token;
                    })
                    .catch(err => {
                        return Promise.reject(err);
                    })
                    .finally(() => {
                        isRefreshing = false;
                        refreshPromise = null;
                    });
            }

            try {
                const newToken = await refreshPromise;
                if (newToken) {
                    originalRequest.headers.Authorization = `Bearer ${newToken}`;
                    return api(originalRequest);
                }
            } catch (refreshError) {
                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);


function getCookie(name: string): string | null {
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    if (match) return match[2];
    return null;
}