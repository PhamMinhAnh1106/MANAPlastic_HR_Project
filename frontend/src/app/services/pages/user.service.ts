import { information } from "../../interface/user/user.interface";
import { api } from "../api.service";

interface updateaccount extends Pick<information, "fullname" | "cccd" | "email" | "phonenumber" | "gender" | "birth" |
    "address" | "bankAccount" | "bankName"> { }
export async function UpdateAccount(formdata: updateaccount, role: string) {
    const roleuser = role.toLowerCase();
    try {
        const res = await api.put(`/${roleuser}/updateProfile`, {
            fullname: formdata.fullname,
            cccd: formdata.cccd == null ? 0 : formdata.cccd,
            email: formdata.email,
            phonenumber: formdata.phonenumber,
            gender: formdata.gender as boolean,
            birth: formdata.birth,
            bankAccount: formdata.bankAccount,
            bankName: formdata.bankName
        });
        return {
            data: res.data,
            status: res.status
        };
    } catch (e) {
        return `loi he thong ` + e;
    }
}

export async function changePassword(oldPassword: string, newPassword: string, role: string) {
    const roleuser = role.toLowerCase();
    try {
        const res = await api.put(`/${roleuser}/changePass`, { oldPassword, newPassword });
        return {
            data: res.data,
            status: res.status
        };
    } catch (e) {
        return "co loi xay ra" + e;
    }
}

