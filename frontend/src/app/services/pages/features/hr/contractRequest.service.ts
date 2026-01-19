import { api } from "../../../api.service";



export async function getContractRequests(keyword: string) {
    try {
        const res = await api.get(`/user/contractRequests?keyword=${keyword}`);
        return res.data;
    } catch (error) {
        return error;
    }
}

export async function getContractHistoryVersion(id: number) {
    try {
        const res = await api.get(`/user/contractRequests/history/contract/${id}`);
        return res.data;
    } catch (error) {
        return error;
    }

}

export interface ContractRequest {
    status: 'APPROVED' | 'REJECTED';
    adminNote: string;
}
export async function ManageContractRequest(id: number, body: ContractRequest) {
    try {
        const res = await api.put(`/user/contractRequests/${id}/status`, body);
        return {
            data: res.data,
            status: res.status
        }
    } catch (error) {
        return error;
    }
}


export interface CreateContractrequest {
    contractId: number;
    reason: string;
    file: File;
}
export async function CreateContractRequest(body: CreateContractrequest) {
    try {
        const formData = new FormData();
        formData.append('contractId', body.contractId.toString());
        formData.append('reason', body.reason);
        formData.append('file', body.file);

        const res = await api.post('/user/contractRequests', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });

        return res.data;
    } catch (error) {
        console.error("Lỗi khi gửi yêu cầu:", error);
        throw error;
    }
}