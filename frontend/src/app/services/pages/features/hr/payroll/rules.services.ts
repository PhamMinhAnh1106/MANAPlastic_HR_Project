// Giả sử đường dẫn tới api.service của bạn
import { api } from "../../../../../services/api.service";

// --- RULES API ---
export async function getRules() {
    try {
        const res = await api.get('/admin/payEngine/rules');
        return res.data;
    } catch (error) {
        console.error("Get Rules Error:", error);
        return null;
    }
}

export async function saveRule(payload: any) {
    try {
        const res = await api.post('/admin/payEngine/rules', payload);
        return res.data;
    } catch (error) {
        throw error; // Ném lỗi để component bắt và hiển thị alert
    }
}

export async function deleteRule(ruleId: number) {
    try {
        const res = await api.delete(`/admin/payEngine/rules/${ruleId}`);
        return res.data;
    } catch (error) {
        throw error;
    }
}

// --- VARIABLES API ---
export async function getVariables() {
    try {
        const res = await api.get('/admin/payEngine/variables');
        return res.data;
    } catch (error) {
        console.error("Get Variables Error:", error);
        return null;
    }
}

export async function saveVariable(payload: any) {
    try {
        const res = await api.post('/admin/payEngine/variables', payload);
        return res.data;
    } catch (error) {
        throw error;
    }
}

export async function deleteVariable(varId: number) {
    try {
        const res = await api.delete(`/admin/payEngine/variables/${varId}`);
        return res.data;
    } catch (error) {
        throw error;
    }
}

// --- AUDIT / SIMULATOR API ---
export async function getAuditUsers() {
    try {
        const res = await api.get('/admin/payEngine/variables/userListAudit');
        return res.data;
    } catch (error) {
        return [];
    }
}

export async function auditVariable(payload: any) {
    try {
        const res = await api.post('/admin/payEngine/variables/audit', payload);
        return res.data;
    } catch (error) {
        throw error;
    }
}