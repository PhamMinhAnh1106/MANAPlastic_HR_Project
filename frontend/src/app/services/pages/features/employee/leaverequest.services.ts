import { leaverequestRegister } from "../../../../interface/leaverequest.interface";
import { api } from "../../../api.service";
//nhan vien
//lay don phep nghi
export async function getleaverequest() {
    try {
        const res = await api.get("/user/leaverequest/myRequest");
        return res.data;
    } catch (error) {
        return "co loi xay ra " + error;
    }
}
// lay thong tin ngay phep nghi con lai
export async function getBalanceleaverequest() {
    try {
        const res = await api.get("/user/leaverequest/myBalances");
        return res.data;
    } catch (error) {
        return "co loi xay ra " + error;
    }
}



//them don phep
export async function Registerleaverequest(forms: leaverequestRegister) {
    try {
        const res = await api.post("/user/leaverequest/addRequest", {
            leavetype: forms.leavetype,
            startdate: forms.startdate,
            enddate: forms.enddate,
            reason: forms.reason
        });
        return {
            status: res.status,
            data: res.data
        }
    } catch (error) {
        return "co loi xay ra " + error;
    }
}
//xoa don phep
export async function Deleteleaverequest(idRequest: number) {
    try {
        const res = await api.delete(`/user/leaverequest/myRequest/${idRequest}`);
        return {
            status: res.status,
            data: res.data
        }
    } catch (error) {
        return "co loi xay ra " + error;
    }
}

///////////////////////// hr , manager
export async function getleaverequestManage(username: string) {
    try {
        const res = await api.get(`/user/leaverequest/filter?username=${username}`);
        return res.data;

    } catch (error) {
        return "co loi xay ra " + error;
    }
}
export async function Approveleaverequest(idRequest: number) {
    try {
        const res = await api.patch(`/user/leaverequest/approve/${idRequest}`);
        return {
            status: res.status,
            data: res.data
        }
    } catch (error) {
        return "co loi xay ra " + error;
    }
}
export async function Rejectleaverequest(idRequest: number) {
    try {
        const res = await api.patch(`/user/leaverequest/approve/${idRequest}`);
        return {
            status: res.status,
            data: res.data
        }
    } catch (error) {
        return "co loi xay ra " + error;
    }
}