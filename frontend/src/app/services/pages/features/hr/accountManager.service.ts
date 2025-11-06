import { information } from "../../../../interface/user/user.interface";
import { api } from "../../../api.service";


export async function GetAccountInfo(id: number, role: string) {
    try {
        const res = await api.get(`/${role.toLowerCase()}/user/${id}`, {});
        return res.data;
    } catch (error) {
        return "co loi xay ra " + error;
    }
}

export async function UpdateAccounthr(formdata: information) {
    try {
        const res = await api.put(`/hr/user/${formdata.userID}`, {
            fullname: formdata.fullname,
            cccd: formdata.cccd,
            email: formdata.email,
            phonenumber: formdata.phonenumber,
            gender: formdata.gender,
            birth: formdata.birth,
            address: formdata.address,
            bankAccount: formdata.bankAccount,
            bankName: formdata.bankName,
            hireDate: formdata.hireDate,
            roleName: formdata.roleName,
            departmentID: formdata.departmentID
        });
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return "co loi xay ra " + error;

    }
}