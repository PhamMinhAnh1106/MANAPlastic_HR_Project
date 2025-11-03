import { api } from "../api.service";

const role_name = [
    "Admin",
    "Employee",
    "HR",
    "Manager"
] as const;
type singleRole = typeof role_name[number];

export async function getdataRole(role: string) {
    const checkrole = role_name.includes(role as singleRole);
    if (checkrole) {
        switch (role) {
            case "Admin":
                return await AdminHomepage();

            case "Employee":
                return await EmployeeHomepage();


            case "HR":
                return await HrHomepage();


            case "Manager":
                return await ManagerHomepage();


        }
    }
}

async function AdminHomepage() {
    try {
        const res = await api.get("/admin/profile");
        return res.data;
    } catch (e) {
        return e;
    }
}
async function HrHomepage() {
    try {
        const res = await api.get("/hr/profile");
        return res.data;
    } catch (e) {
        return e;
    }
}
async function EmployeeHomepage() {
    try {
        const res = await api.get("/employee/profile");
        return res.data;
    } catch (e) {
        return e;
    }
}
async function ManagerHomepage() {
    try {
        const res = await api.get("/manager/profile");
        return res.data;
    } catch (e) {
        return e;
    }
}