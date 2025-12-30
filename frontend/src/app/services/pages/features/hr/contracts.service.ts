
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
export interface EditContractInterface {
    id: number;
    username: string;
    contractName: string;
    type: string;
    baseSalary: number;
    insuranceSalary: number;
    allowanceToxicType: string;
    signDate: string;
    startDate: string;
    endDate: string;
    status: string;
    file?: File | null;
}

// [UPDATE] Hàm gọi API dùng FormData
export async function EditContract(form: EditContractInterface) {
    try {
        const formData = new FormData();
        formData.append('id', form.id.toString());
        formData.append('username', form.username);
        formData.append('contractName', form.contractName);
        formData.append('type', form.type);
        formData.append('baseSalary', form.baseSalary.toString());
        formData.append('insuranceSalary', form.insuranceSalary.toString());
        formData.append('allowanceToxicType', form.allowanceToxicType);
        formData.append('signDate', form.signDate);
        formData.append('startDate', form.startDate);
        if (form.endDate) formData.append('endDate', form.endDate);
        formData.append('status', form.status);

        // Chỉ append file nếu có chọn file mới
        if (form.file) {
            formData.append('file', form.file);
        }

        const res = await api.put(`/hr/contracts/${form.id}`, formData);

        return {
            data: res.data,
            status: res.status
        };

    } catch (error) {
        return error;
    }
}