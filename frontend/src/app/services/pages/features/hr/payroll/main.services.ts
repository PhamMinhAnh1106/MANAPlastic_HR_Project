import { api } from "../../../../api.service";

export async function calculatePayroll(month: string, year: string) {
    try {
        const res = await api.post(`/payroll/calculate?month=${month}&year=${year}`);
        return res.data;
    } catch (error) {
        return error;
    }
}

export async function RulesPayroll(empId: number, month: string, year: string) {
    try {
        const res = await api.get(`/payroll/debug-details?employeeId=${empId}&month=${month}&year=${year}`);
        return res.data;
    } catch (error) {
        return error;
    }
}
export async function Payrollsfinalize(month: string, year: string) {
    try {
        const res = await api.get(`/payroll/finalize?month=${month}&year=${year}`);
        return res.data;
    } catch (error) {
        return error;
    }
}