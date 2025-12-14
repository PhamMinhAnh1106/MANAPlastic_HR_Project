import { api } from "../../../../../services/api.service";

export async function getMyPayslip(month: number, year: number) {
    try {
        const res = await api.get('/user/payroll/my-payslip', {
            params: {
                month: month,
                year: year
            }
        });
        return res.data;
    } catch (error) {
        console.error("Get My Payslip Error:", error);
        // Bạn có thể return null hoặc throw error tùy vào cách bạn muốn xử lý ở UI
        throw error;
    }
}

export async function getUserPayrollDetail(id: number | string, month: number, year: number) {
    try {
        // Truyền id vào đường dẫn (path variable) và month/year vào query params
        const res = await api.get(`/user/payroll/${id}`, {
            params: {
                month: month,
                year: year
            }
        });
        return res.data;
    } catch (error) {
        console.error(`Get User Payroll (ID: ${id}) Error:`, error);
        throw error;
    }
}
export async function Fillterpayslip(params: string) {
    try {
        // Truyền id vào đường dẫn (path variable) và month/year vào query params
        const res = await api.get(`/user/payroll/filter?${params}`);
        return res.data;
    } catch (error) {
        throw error;
    }
}