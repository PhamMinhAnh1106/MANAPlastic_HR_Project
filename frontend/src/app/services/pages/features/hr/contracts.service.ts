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
        return error;
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
        return error;
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
        return error;
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
        return error;
    }
}

export async function getNotificationContract() {
    try {
        const res = await api.get(`/hr/contracts/expiringNoti`);
        return res.data;
    } catch (error) {
        return error;
    }
}


// Interface mở rộng để bao gồm ID phục vụ cho việc gọi API PUT
export interface EditContractParams extends CreateContractPayload {
    id: number;
}

// 2. Hàm EditContract đã chỉnh sửa
export async function EditContract(form: EditContractParams) {
    try {
        // Tách ID ra khỏi payload để dùng cho URL, phần còn lại là body
        const { id, ...payload } = form;

        // Gọi API với method PUT và gửi body dạng JSON
        // Lưu ý: Đảm bảo headers của axios/fetch là 'Content-Type': 'application/json'
        const res = await api.put(`/hr/contracts/${id}`, payload);

        return {
            data: res.data,
            status: res.status
        };

    } catch (error) {
        return error;
    }
}
// phần hợp đồng mới
export interface contracttemplate {
    id: number;
    templateID: number;
    name: string;
    type: string;
    content: string;
}
export async function getcontracttemplate() {
    try {
        const res = await api.get(`/hr/contract/templates`);
        return res.data;
    } catch (error) {
        return error;
    }
}

//Lấy chi tiết một mẫu hợp đồng
export async function getcontracttemplatebyid(id: number) {
    try {
        const res = await api.get(`/hr/contract/templates/${id}`);
        return res.data;
    } catch (error) {
        return error;
    }
}
//Cập nhật mẫu hợp đồng

export interface contracttemplateupdate {
    templateID: number;
    name: string;
    type: string;
    content: string;
}

export async function updatecontracttemplate(id: number, template: contracttemplateupdate) {
    try {
        const res = await api.put(`/hr/contract/templates/${id}`, template);
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return error;
    }
}

//Tạo mới mẫu hợp đồng

export interface contracttemplatecreate {
    name: string;
    type: string;
    content: string;
}
export async function createcontracttemplate(template: contracttemplatecreate) {
    try {
        const res = await api.post(`/hr/contract/templates`, template);
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return error;
    }
}
////// trang hợp đồng lao động 

export interface Allowance {
    allowanceName: string;
    allowanceType: string;
    amount: number;
    isTaxable: boolean;
    isInsuranceBase: boolean;
    taxFreeAmount: number;
}
export interface CreateContractPayload {
    userId: number | null;
    departmentId: number;
    roleId: number;
    fullname: string;
    cccd: string;
    email: string;
    phone: string;
    address: string;
    dob: string
    gender: string;
    contractType: string;
    workType: string;
    templateId: number;
    startDate: string;
    endDate: string | null
    baseSalary: number;
    insurancePercent: number;
    allowanceToxicType: string;
    allowances: Allowance[];
}

export async function createContract(payload: CreateContractPayload) {
    try {
        const res = await api.post(`/hr/contracts/create`, payload);
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return error;
    }
}

export async function getContractPdfUrl(contractId: number) {
    try {
        // QUAN TRỌNG: Thêm responseType: 'blob' để axios hiểu đây là file binary
        const response = await api.get(`/hr/contracts/${contractId}/print`, { // Dùng endpoint /print như postman log của bạn
            responseType: 'blob'
        });

        // Trả về dữ liệu Blob gốc thay vì tạo URL string tại đây
        return response.data;
    } catch (error: any) {
        console.error("Lỗi tải PDF:", error);
        // Kiểm tra xem lỗi là do Auth hay do server
        if (error.response && error.response.status === 401) {
            return "co loi xay ra: Hết phiên đăng nhập";
        }
        return (error.message || error);
    }
}