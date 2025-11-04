import { api } from "../../../api.service";

interface addaccount {
    fullname: string,
    cccd: bigint,
    role: number
}
export async function addAccount(formdata: addaccount) {
    const roleName = ["Admin", "HR", "Manager", "Employee"]

    try {
        const res = await api.post("/admin/addAccount", {
            fullname: formdata.fullname,
            cccd: formdata.cccd, roleID: {
                id: formdata.role,
                rolename: roleName[formdata.role - 1]
            }, status: "active"
        });
        return {
            data: res.data,
            status: res.status
        }
    } catch (e) {
        return "co loi xay ra " + e;
    }
}