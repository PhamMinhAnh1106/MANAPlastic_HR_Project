import { api } from "../../../api.service";

interface addaccount {
    fullname: string,
    cccd: string,
    gender: string,
    role: number,
    department: number
}
export async function addAccount(formdata: addaccount) {
    const roleName = ["Admin", "HR", "Manager", "Employee"]

    try {
        const res = await api.post("/admin/addAccount", {
            fullname: formdata.fullname,
            cccd: formdata.cccd,
            gender: formdata.gender,
            roleID: {
                id: formdata.role,
                rolename: roleName[formdata.role - 1]
            },
            status: "active",
            departmentID: {
                id: formdata.department
            }
        });
        return {
            data: res.data,
            status: res.status
        }
    } catch (e) {
        return "co loi xay ra " + e;
    }
}

export async function ActiveaddAccount(pdfFile: File) {
    try {
        const formData = new FormData();

        formData.append("file", pdfFile);
        // Gửi request
        const res = await api.post("/admin/addAccount", formData);
        return {
            data: res.data,
            status: res.status
        }
    } catch (e) {
        return "co loi xay ra " + e;
    }
}