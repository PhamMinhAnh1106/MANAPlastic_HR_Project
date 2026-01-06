export interface information {
    userID?: number,
    username?: string,
    password?: string,
    fullname: string,
    cccd: string,
    email: string,
    phonenumber: string,
    gender: string,
    birth: string,
    address: string,
    bankAccount: string,
    bankName: string,
    hireDate: string,
    status: string,
    roleName: string,
    departmentID: number
}
export interface department {
    departmentId: number,
    departmentName: string
}
export const Department: department[] = [
    { departmentId: 1, departmentName: "Phòng Ban Nhân Sự" },
    { departmentId: 2, departmentName: "Phòng Ban IT" },
    { departmentId: 3, departmentName: "Phòng Ban Kỹ Thuật" },
    { departmentId: 4, departmentName: "Phòng Ban Sản Xuất" },
    { departmentId: 5, departmentName: "Phòng Ban In Ấn" },
    { departmentId: 6, departmentName: "Phòng Ban Chăm Sóc Khách Hàng" }
]

export interface role {
    roleId: number,
    roleName: string
}
export const Role: role[] = [
    { roleId: 1, roleName: "Admin" },
    { roleId: 2, roleName: "HR" },
    { roleId: 3, roleName: "Manager" },
    { roleId: 4, roleName: "Employee" },
]