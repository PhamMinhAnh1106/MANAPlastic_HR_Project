import { api } from "../api.service";

const role_name = [
    "ROLE_Admin",
    "ROLE_Employee",
    "ROLE_HR",
    "ROLE_Manager"
] as const;
type singleRole = typeof role_name[number];

export async function getdataRole(role: string) {
    const checkrole = role_name.includes(role as singleRole);
    if (checkrole) {
        switch (role) {
            case "ROLE_Admin":
                return await AdminHomepage();

            case "ROLE_Employee":
                return await EmployeeHomepage();


            case "ROLE_HR":
                return await HrHomepage();


            case "ROLE_Manager":
                return await ManagerHomepage();


        }
    }
}

async function AdminHomepage() {
    try {
        const res = await api.get("/admin/homePage");
        return res.data;
    } catch (e) {
        return e;
    }
}
async function HrHomepage() {
    try {
        const res = await api.get("/hr/homePage");
        return res.data;
    } catch (e) {
        return e;
    }
}
async function EmployeeHomepage() {
    try {
        const res = await api.get("/employee/homePage");
        return res.data;
    } catch (e) {
        return e;
    }
}
async function ManagerHomepage() {
    try {
        const res = await api.get("/manager/homePage");
        return res.data;
    } catch (e) {
        return e;
    }
}