
import { contracts, StatusContract, statusContract } from "../../../../interface/contract.interface";
import { api } from "../../../api.service";
interface checkcontract {
    allowFixedTerm: boolean,
    message: string,
    userId: number
}
export async function CheckContractByIdEmployee(id: number) {
    try {
        const res = await api.get(`/hr/contracts/checkRenewal/${id}`);
        const data: checkcontract = res.data;
        return {
            data: data.message,
            status: res.status
        }
    } catch (error) {
        return "co loi xay ra " + error;
    }
}

export async function FillterContract(query: string, page: number, size: number) {
    try {
        const res = await api.get(`/hr/contracts/contractFilter?page=${page}&size=${size}&${query}`);
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return "co loi xay ra " + error;
    }
}
export async function FillterContractByIdEmployee(id: number) {
    try {
        const res = await api.get(`/hr/contracts/user/${id}`);
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return "co loi xay ra " + error;
    }
}
export async function ExportFileDataContracts(query: string) {
    try {
        const res = await api.get(`/hr/contract/exportExcel?${query}`, {
            responseType: 'blob', // bắt buộc nếu API trả file
        });

        // Tạo link download
        const url = window.URL.createObjectURL(res.data);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Data_contracts.xlsx`;
        a.click();
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error("Có lỗi xảy ra:", error);
    }
}

export async function ExportFileDataAttendance(query: string) {
    try {
        const res = await api.get(`/hr/attendace/exportExcel?${query}`, {
            responseType: 'blob',
        });

        const url = window.URL.createObjectURL(res.data);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Data_Attendance.xlsx`;
        a.click();
        window.URL.revokeObjectURL(url);

    } catch (error) {
        console.error("Có lỗi xảy ra:", error);
    }
}
export async function AddNewContract(formData: FormData) {
    try {
        const res = await api.post("/hr/contracts/create", formData, {
            headers: {
                // **KHÔNG** set 'application/json', axios sẽ tự set multipart/form-data
                'Accept': 'application/json'
            }
        });
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return "co loi xay ra " + error;
    }
}

export async function getNotificationContract() {
    try {
        const res = await api.get(`/hr/contracts/expiringNoti`);
        return res.data;
    } catch (error) {
        return "co loi xay ra " + error;
    }
}