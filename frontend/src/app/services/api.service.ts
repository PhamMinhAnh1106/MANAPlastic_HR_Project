import axios from "axios";
import { refreshAccessToken } from "../utils/token.utils";
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
api.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
        if (error.response) {
            if (error.response.status === 403) {
                console.warn("Access token đã hết hạn hoặc không hợp lệ.");



                // Hoặc nếu bạn có refresh token API, có thể gọi ở đây
                const refreshtoken = getCookie('refreshToken');
                if (refreshtoken != null) {

                    const res = await refreshAccessToken(refreshtoken) as { token: string, refreshToken: string };
                    document.cookie = `access_token=${res.token}; path=/;`
                    document.cookie = `refreshToken=${res.refreshToken}; path=/;`

                }
            }
        }

        // Nếu là lỗi khác, vẫn trả về để xử lý tiếp
        return Promise.reject(error);
    }
);

function getCookie(name: string): string | null {
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    if (match) return match[2];
    return null;
}


