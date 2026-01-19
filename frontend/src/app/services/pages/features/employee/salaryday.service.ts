import { api } from "../../../api.service";


export interface GetDateSalaryI {
    amount: number,
    message: string
}

export async function GetDateSalary() {
    try {
        const res = await api.get("/user/DailySalary/daily");
        return res.data;
    } catch (error) {
        return error;
    }
}

export interface GetMonthlySalaryI {
    month: number,
    year: number,
    userId: number,
    totalEstimatedSalary: number
}

export async function GetMonthlySalary(month: number, year: number) {
    try {
        const res = await api.get(`/user/DailySalary/monthlyTotal?month=${month}&year=${year}`);
        return res.data;
    } catch (error) {
        return error;
    }
}

export async function RecalculateSalary(month: number, year: number) {
    try {
        const res = await api.post(`/user/DailySalary/recalculate?month=${month}&year=${year}`);
        return res.data;
    } catch (error) {
        return error;
    }
}