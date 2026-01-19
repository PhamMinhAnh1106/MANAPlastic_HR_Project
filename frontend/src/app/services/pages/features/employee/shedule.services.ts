import { ChangeSchedule, schedule } from "../../../../interface/schedule.interface";
import { api } from "../../../api.service";

export interface specificDays {
    date: string,
    shiftId: number,
    dayoff?: boolean
}

export interface registerShiftRoute {
    fromDate: string,
    toDate: string,
    rangeShiftId: number
    specificDays?: specificDays[]
}
export async function RegisterScheduleEmployee(forms: registerShiftRoute) {
    try {
        const res = await api.post("/user/shiftSchedule/myDraft", forms)
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}
export async function GetScheduleEmployeeDraft(month_year: string) {
    try {
        const res = await api.get(`/user/shiftSchedule/myDraft?month_year=${month_year}`)
        return res.data;
    } catch (error) {
        return error;
    }
}
export async function GetScheduleEmployeeoffice(month_year: string) {
    try {
        const res = await api.get(`/user/shiftSchedule/myOfficial?month_year=${month_year}`,)
        return res.data;
    } catch (error) {
        return error;
    }
}
export async function GetScheduleManagerdraft(month_year: string) {
    try {
        const res = await api.get(`/manager/shiftSchedule/drafts?month_year=${month_year}`,)
        return res.data;
    } catch (error) {
        return error;
    }
}
export async function GetScheduleManageroffice(month_year: string) {
    try {
        const res = await api.get(`/manager/shiftSchedule/official?month_year=${month_year}`,)
        return res.data;
    } catch (error) {
        return error;
    }
}

export async function ChangeScheduleDraftManager(forms: ChangeSchedule) {
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
        return error;
    }
}
// export async function ChangeScheduleOfficeManager(forms: ChangeSchedule) {
//     try {
//         const res = await api.put("/manager/shiftSchedule/official/batch", [{
//             employeeId: forms.employeeId,
//             date: forms.date,
//             shiftId: forms.shiftId,
//             isDayOff: forms.isDayOff
//         }]);
//         return {
//             data: res.data,
//             status: res.status
//         }
//     } catch (error) {
//         return  error;
//     }
// }

export async function UpSchedule(month_year: string) {
    try {
        const res = await api.post("/manager/shiftSchedule/finalize", { month_year: month_year })
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return error;

    }
}

export interface rescheduleinterface {
    targetDate: string,
    newShiftId: number,
    reason: string
}
export async function RescheduleAPI(form: rescheduleinterface) {
    try {
        const res = await api.post("/user/shiftChanges", form);
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return error;
    }
}



export async function getReschedule(query: string) {
    try {
        if (query != '') {
            const res = await api.get(`/user/shiftChanges/filter?${query}`);
            return res.data;
        }
        const res = await api.get(`/user/shiftChanges/filter`);
        return res.data;

    } catch (error) {
        return error;
    }
}

export async function ApproveReschedule(id: number) {
    try {
        const res = await api.put(`/user/shiftChanges/approve/${id}`);
        return res.data;

    } catch (error) {
        return error;
    }
}

export async function RejectReschedule(id: number) {
    try {
        const res = await api.put(`/user/shiftChanges/reject/${id}`);
        return res.data;

    } catch (error) {
        return error;
    }
}



export interface NewEmployeeInterface {
    username: string,
    startDate: string,
    shiftId: number,
    specificDays?: specificDays[]
}
export async function AddNewEmployee(form: NewEmployeeInterface) {
    try {
        const res = await api.post("/manager/shiftSchedule/newEmployee", form);
        return {
            data: res.data,
            status: res.status
        };

    } catch (error) {
        return error;
    }
}

