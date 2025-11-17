import { ChangeSchedule, schedule } from "../../../../interface/schedule.interface";
import { api } from "../../../api.service";



export async function RegisterScheduleEmployee(forms: schedule) {
    try {
        const res = await api.post("/user/shiftSchedule/myDraft", [
            {
                date: forms.date,
                shiftId: forms.shiftId,
                isDayOff: forms.isDayOff
            }
        ])
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return `co loi xay ra ` + error;
    }
}
export async function GetScheduleEmployeeDraft(month_year: string) {
    try {
        const res = await api.get(`/user/shiftSchedule/myDraft?month_year=${month_year}`)
        return res.data;
    } catch (error) {
        return `co loi xay ra ` + error;
    }
}
export async function GetScheduleEmployeeoffice(month_year: string) {
    try {
        const res = await api.get(`/user/shiftSchedule/myOfficial?month_year=${month_year}`,)
        return res.data;
    } catch (error) {
        return `co loi xay ra ` + error;
    }
}
export async function GetScheduleManagerdraft(month_year: string) {
    try {
        const res = await api.get(`/manager/shiftSchedule/drafts?month_year=${month_year}`,)
        return res.data;
    } catch (error) {
        return `co loi xay ra ` + error;
    }
}
export async function GetScheduleManageroffice(month_year: string) {
    try {
        const res = await api.get(`/manager/shiftSchedule/official?month_year=${month_year}`,)
        return res.data;
    } catch (error) {
        return `co loi xay ra ` + error;
    }
}

export async function ChangeScheduleManager(forms: ChangeSchedule) {
    try {
        const res = await api.post("/manager/shiftSchedule/drafts/batch", [{
            employeeId: forms.employeeId,
            date: forms.date,
            shiftId: forms.shiftId,
            isDayOff: forms.isDayOff
        }]);
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return `co loi xay ra ` + error;
    }
}

export async function UpSchedule(month_year: string) {
    try {
        const res = await api.post("/manager/shiftSchedule/finalize", { month_year: month_year })
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return `co loi xay ra ` + error;

    }
}