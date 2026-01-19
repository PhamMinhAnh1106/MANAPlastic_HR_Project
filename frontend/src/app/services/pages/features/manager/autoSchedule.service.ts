import { reqAutoSchedule } from "../../../../interface/autoSchedule";
import { api } from "../../../api.service";

export async function RequirementsAutoSchedule(forms: reqAutoSchedule) {
    try {
        const res = await api.post("/manager/requirements", forms);
        return res.status;
    } catch (error) {
        return error;
    }
}

export async function AutoAssignSchedule(month_year: string) {
    try {
        const res = await api.post("/manager/shiftSchedule/auto-assign", { month_year });
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return error;
    }
}

export async function GetRequirementsAutoSchedule() {
    try {
        const res = await api.get("/manager/requirements");
        return res.data;
    } catch (error) {
        return error;
    }
}

export async function CheckAutoAssignSchedule(month_year: string) {
    try {
        const res = await api.get(`/manager/shiftSchedule/drafts/validate?month_year=${month_year}`);
        return res.data;
    } catch (error) {
        return error;
    }
}

interface formRule {
    requirementId: number,
    totalStaffNeeded: number,
    rules:
    {
        requiredSkillGrade: number,
        minStaffCount: number
    }[]

}

export async function updateRuleData(form: formRule) {
    try {
        const res = await api.put(`/manager/requirements/${form.requirementId}`, form);
        return res.data;
    } catch (error) {
        return error;
    }
}