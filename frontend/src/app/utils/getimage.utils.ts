import { api } from "../services/api.service";
export async function getContractFile(name: string) {
    try {
        // FIX: Thêm dấu '/' trước ${name}
        const res = await api.get(`/hr/contracts/files/${name}`, {
            responseType: 'blob', // Quan trọng: Giữ nguyên để nhận file binary
        });

        // Trả về dữ liệu Blob
        return res.data;
    } catch (error) {
        console.error("Lỗi tải hợp đồng:", error);
        return null;
    }
}

export async function getImageChamcong(name: string) {
    try {
        const res = await api.get(`/chamCong/images${name}`, {
            responseType: 'blob',
        });
        return res.data;
    } catch (error) {
        return error;
    }
}
export async function getImageAttendance(name: string) {
    try {
        const res = await api.get(`/user/attendanceRequests/images/${name}`, {
            responseType: 'blob',
        });
        return res.data;
    } catch (error) {
        return error;
    }
}
