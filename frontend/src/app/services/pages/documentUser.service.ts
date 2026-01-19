import { api } from "../api.service";
export interface DocumentItem {
    documentID: number;
    employeeName?: string;
    type: string;
    name: string;
    status: string;
    fileUrl: string;
    expiryDate: string | null;
    issuingAuthority?: string;
    issueDate?: string;
    note?: string;
    userId?: number;
}
//  HR
///appprove /reject hồ sơ
export interface ApproveDocument {

    note: string;
    status: string;
}
export async function getDocumentsByUserId(data: ApproveDocument, documentID: number) {
    try {
        const res = await api.put(`/user/documents/${documentID}/status`, ({
            note: data.note,
            status: data.status
        }));
        return {
            data: res.data,
            status: res.status
        };
    } catch (error) {
        return error;
    }
}
/// danh sách hồ sơ cần duyệt

export async function getListDocumentEmployee() {
    try {
        const res = await api.get("/user/documents/list");
        return res.data;
    } catch (error) {
        return error;
    }
}
export async function getDocumentEmployeebyid(userID: number, keyword: string) {
    try {
        const res = await api.get(`/user/documents/user/${userID}?keyword=${keyword}`);
        return res.data;
    } catch (error) {
        return error;
    }
}


// //////////////////////////////
// get hồ sơ bản thân
export async function getMyDocuments() {
    try {
        const res = await api.get("/user/documents/myDocuments");
        return res.data;
    } catch (error) {
        return error;
    }
}
export async function getDocumentFile(filename: string): Promise<Blob | null> {
    try {
        const res = await api.get(`/user/documents/files/${filename}`, {
            responseType: 'blob', // Quan trọng: Báo cho axios biết đây là dữ liệu nhị phân
        });

        // Tạo Blob mới kèm theo type là application/pdf để đảm bảo trình duyệt hiểu đúng
        // ngay cả khi server trả về header không chuẩn.
        return new Blob([res.data], { type: 'application/pdf' });
    } catch (error) {
        console.error("Lỗi khi tải file PDF:", error);
        return null; // Hoặc throw error tùy cách bạn handle lỗi
    }
}


export async function openPdfViewer(filename: string) {
    const pdfBlob = await getDocumentFile(filename);

    if (pdfBlob) {

        const fileUrl = window.URL.createObjectURL(pdfBlob);

        window.open(fileUrl, '_blank');
    } else {
        alert("Không thể tải file PDF.");
    }
}

//// upload hồ sơ có file pdf
export interface UploadDocument {
    file: File;
    userId: number;
    documentType: string;
    documentName: string;
    issuingAuthority: string;
    issueDate: string;
    expiryDate: string;
    note: string;
}

export async function uploadProfile(data: UploadDocument) {
    try {
        const formData = new FormData();

        formData.append("file", data.file);
        formData.append("userId", data.userId.toString());
        formData.append("documentType", data.documentType);
        formData.append("documentName", data.documentName);
        formData.append("issuingAuthority", data.issuingAuthority);
        formData.append("issueDate", data.issueDate);
        formData.append("expiryDate", data.expiryDate);
        formData.append("note", data.note);


        const res = await api.post("/user/documents/upload", formData, {
            headers: {

                "Content-Type": "multipart/form-data",
            },
        });

        return {
            data: res.data,
            status: res.status
        };
    } catch (e) {

        console.error("Upload error:", e);
        throw e;
    }
}

