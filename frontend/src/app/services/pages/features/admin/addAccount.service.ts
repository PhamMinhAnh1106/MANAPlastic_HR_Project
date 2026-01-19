import { api } from "../../../api.service";


export async function ActiveaddAccount(pdfFile: File, id: number) {
    try {
        const formData = new FormData();

        formData.append("file", pdfFile);
        // Gửi request
        const res = await api.post(`/admin/addAccount/${id}`, formData);
        return {
            data: res.data,
            status: res.status
        }
    } catch (e) {
        return e;
    }
}